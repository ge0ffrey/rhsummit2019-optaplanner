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
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.redhat.summit2019.optaplanner.domain.MachineComponent;
import com.redhat.summit2019.optaplanner.domain.TravelingMechanicSolution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class TravelingMechanicPanel extends SolutionPanel<TravelingMechanicSolution> {

    private TravelingMechanicWorldPanel travelingMechanicWorldPanel;

    public TravelingMechanicPanel() {
        setLayout(new BorderLayout());
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton button = new JButton("Dummy button");
        buttonPanel.add(button);
        add(buttonPanel, BorderLayout.NORTH);
        travelingMechanicWorldPanel = new TravelingMechanicWorldPanel(this);
        add(travelingMechanicWorldPanel, BorderLayout.CENTER);
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
