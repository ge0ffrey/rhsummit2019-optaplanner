/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.summit2019.optaplanner.swingui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;

import com.redhat.summit2019.optaplanner.domain.MachineComponent;
import com.redhat.summit2019.optaplanner.domain.Mechanic;
import com.redhat.summit2019.optaplanner.domain.TravelingMechanicSolution;
import com.redhat.summit2019.optaplanner.domain.Visit;
import org.optaplanner.examples.common.swingui.SolutionPanel;

public class TravelingMechanicPanel extends SolutionPanel<TravelingMechanicSolution> {

    public static final int REFRESH_RATE_MILLIS = 40;

    private TravelingMechanicWorldPanel travelingMechanicWorldPanel;

    private JSpinner playerSizeField;
    private JSpinner attritionPerMilliSecondField;
    private JSpinner mechanicSpeedPerMillisField;
    private AbstractAction simulateAction;
    private Timer timer;
    private Random swingThreadRandom = new Random(37);
    private Random solverThreadRandom = new Random(37);

    private volatile int playerSize = Integer.MIN_VALUE;
    private volatile List<MachineComponent> shuffledMachineComponentList = null;
    private volatile double attritionPerRefresh = Double.NaN;
    private long globalTimeMillis = Long.MIN_VALUE;

    public TravelingMechanicPanel() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(new JLabel("Player size:"));
        playerSizeField = new JSpinner(new SpinnerNumberModel(20, 0, 5000, 10));
        buttonPanel.add(playerSizeField);
        buttonPanel.add(new JLabel("Avg attrition per ms per player:"));
        attritionPerMilliSecondField = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.000_05));
        ((JSpinner.NumberEditor) attritionPerMilliSecondField.getEditor()).getFormat().setMinimumFractionDigits(6); // Hack
        attritionPerMilliSecondField.setPreferredSize(
                new Dimension(100, attritionPerMilliSecondField.getPreferredSize().height)); // Hack
        buttonPanel.add(attritionPerMilliSecondField);
        buttonPanel.add(new JLabel("Mechanic speed per ms:"));
        mechanicSpeedPerMillisField = new JSpinner(new SpinnerNumberModel(0.1, 0.0, 10.0, 0.01));
        buttonPanel.add(mechanicSpeedPerMillisField);

        timer = new Timer(REFRESH_RATE_MILLIS, e -> refreshSimulation());
        simulateAction = new AbstractAction("Simulate") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!timer.isRunning()) {
                    TravelingMechanicSolution solution = getSolution();
                    if (solution == null) {
                        return;
                    }
                    playerSizeField.setEnabled(false);
                    attritionPerMilliSecondField.setEnabled(false);
                    mechanicSpeedPerMillisField.setEnabled(false);
                    playerSize = (Integer) playerSizeField.getValue();
                    double attritionPerMilliSecond = (Double) attritionPerMilliSecondField.getValue();
                    attritionPerRefresh = attritionPerMilliSecond * REFRESH_RATE_MILLIS;
                    double mechanicSpeedPerMillis = (Double) mechanicSpeedPerMillisField.getValue();
                    doProblemFactChange(scoreDirector -> {
                        for (Mechanic mechanic : scoreDirector.getWorkingSolution().getMechanicList()) {
                            scoreDirector.beforeProblemPropertyChanged(mechanic);
                            mechanic.setSpeedInXYPerMillis(mechanicSpeedPerMillis);
                            scoreDirector.beforeProblemPropertyChanged(mechanic);
                        }
                    });
                    shuffledMachineComponentList = new ArrayList<>(solution.getMachineComponentList());
                    Collections.shuffle(shuffledMachineComponentList);
                    if (playerSize > shuffledMachineComponentList.size()) {
                        JOptionPane.showMessageDialog(TravelingMechanicPanel.this, "The playerSize (" + playerSize
                                + ") has been reduced to machineComponentSize (" + shuffledMachineComponentList.size() + ").");
                        playerSize = shuffledMachineComponentList.size();
                    }
                    globalTimeMillis = 0L;
                    timer.start();
                } else {
                    playerSizeField.setEnabled(true);
                    attritionPerMilliSecondField.setEnabled(true);
                    mechanicSpeedPerMillisField.setEnabled(true);
                    playerSize = Integer.MIN_VALUE;
                    attritionPerRefresh = Double.NaN;
                    shuffledMachineComponentList = null;
                    globalTimeMillis = Long.MIN_VALUE;
                    timer.stop();
                }
            }
        };
        buttonPanel.add(new JToggleButton(simulateAction));
        add(buttonPanel, BorderLayout.NORTH);
        travelingMechanicWorldPanel = new TravelingMechanicWorldPanel(this);
        add(travelingMechanicWorldPanel, BorderLayout.CENTER);
    }

    private void refreshSimulation() {
        globalTimeMillis += REFRESH_RATE_MILLIS;
        // Send along the timeMillis of when this problem fact was created
        long timeMillis = globalTimeMillis;
        doProblemFactChange(scoreDirector -> {
            List<MachineComponent> machineComponentList = this.shuffledMachineComponentList;
            // Only the first sublist of the shuffledMachineComponentList suffer attrition, once per player
            for (int player = 0; player < playerSize; player++) {
                MachineComponent machineComponent = machineComponentList.get(player);
                machineComponent = scoreDirector.lookUpWorkingObject(machineComponent);
                double attrition = machineComponent.getAttrition();
                attrition += (0.5 + solverThreadRandom.nextDouble()) * attritionPerRefresh;
                if (attrition > 1.0) {
                    attrition = 1.0;
                }
                scoreDirector.beforeProblemPropertyChanged(machineComponent);
                machineComponent.setAttrition(attrition);
                scoreDirector.afterProblemPropertyChanged(machineComponent);
            }
            TravelingMechanicSolution solution = scoreDirector.getWorkingSolution();
            for (Mechanic mechanic : solution.getMechanicList()) {
                if (mechanic.isWorkInProgress()) {
                    if (mechanic.getStartTimeMillis() <= timeMillis) {
                        MachineComponent machineComponent = mechanic.getStartMachineComponent();
                        scoreDirector.beforeProblemPropertyChanged(machineComponent);
                        machineComponent.setAttrition(0.0);
                        scoreDirector.afterProblemPropertyChanged(machineComponent);
                        Visit next = mechanic.getNext();
                        if (next != null) {
                            scoreDirector.beforeProblemPropertyChanged(mechanic);
                            mechanic.setStartMachineComponent(next.getMachineComponent());
                            mechanic.setStartTimeMillis(timeMillis + next.getTravelTimeMillisFromPrevious() + Visit.SERVICE_TIME_MILLIS);
                            scoreDirector.afterProblemPropertyChanged(mechanic);
                        }
                    }
                } else {
                    double x = mechanic.getLocationX();
                    double toX = mechanic.getStartMachineComponent().getLocationX();
                    double xDiff = toX - x;
                    double y = mechanic.getLocationY();
                    double toY = mechanic.getStartMachineComponent().getLocationY();
                    double yDiff = toY - y;
                    double ratio = mechanic.getSpeedInXYPerMillis() / Math.sqrt((xDiff * xDiff) + (yDiff * yDiff));
                    if (ratio > 1.0) {
                        ratio = 1.0;
                    }
                    x += xDiff * ratio;
                    y += yDiff * ratio;
                    scoreDirector.beforeProblemPropertyChanged(mechanic);
                    mechanic.setLocationX(x + (xDiff * ratio));
                    mechanic.setLocationY(y + (yDiff * ratio));
                    scoreDirector.afterProblemPropertyChanged(mechanic);
                }
            }
        });
        // Randomly once every second, switch attrition to another machineComponent
        if (swingThreadRandom.nextDouble() < (1.0 / REFRESH_RATE_MILLIS)) {
            int switchCount = 1;
            // Use separate variable newList to avoid race condition
            List<MachineComponent> newList = new ArrayList<>(
                    shuffledMachineComponentList.subList(switchCount, shuffledMachineComponentList.size()));
            newList.addAll(shuffledMachineComponentList.subList(0, switchCount));
            shuffledMachineComponentList = newList;
        }
    }

    @Override
    public boolean isWrapInScrollPane() {
        return false;
    }

    @Override
    public void resetPanel(TravelingMechanicSolution solution) {
        travelingMechanicWorldPanel.resetPanel(solution);
    }

    @Override
    public void updatePanel(TravelingMechanicSolution solution) {
        travelingMechanicWorldPanel.updatePanel(solution);
    }

    public void hurtMachineComponent(MachineComponent machineComponent) {
        doProblemFactChange(scoreDirector -> {
            MachineComponent workingMachineComponent = scoreDirector.lookUpWorkingObject(machineComponent);
            double attrition = workingMachineComponent.getAttrition();
            attrition += (0.1 + Math.random() * 0.2); // Loose between 10% and 30%
            if (attrition > 1.0) {
                attrition = 1.0;
            }
            scoreDirector.beforeProblemPropertyChanged(workingMachineComponent);
            workingMachineComponent.setAttrition(attrition);
            scoreDirector.afterProblemPropertyChanged(workingMachineComponent);
        });
    }
}
