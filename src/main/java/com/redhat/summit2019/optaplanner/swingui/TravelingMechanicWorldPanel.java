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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

import com.redhat.summit2019.optaplanner.domain.MachineComponent;
import com.redhat.summit2019.optaplanner.domain.Mechanic;
import com.redhat.summit2019.optaplanner.domain.TravelingMechanicSolution;
import com.redhat.summit2019.optaplanner.domain.Visit;
import org.optaplanner.examples.common.swingui.latitudelongitude.LatitudeLongitudeTranslator;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.swing.impl.TangoColorFactory;

public class TravelingMechanicWorldPanel extends JPanel {

    public static final Stroke FAT_DASHED_STROKE = new BasicStroke(
            1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {7.0f, 3.0f}, 0.0f);
    public static final Stroke NORMAL_STROKE = new BasicStroke(1.0f);
    public static final Stroke FAT_STROKE = new BasicStroke(2.0f);
    public static final Stroke VERY_FAT_STROKE = new BasicStroke(3.0f);

    private static final int ATTRITION_TEXT_SIZE = 10;
    private static final int DEPARTURE_TEXT_SIZE = 8;

    private final TravelingMechanicPanel travelingMechanicPanel;

    private BufferedImage canvas = null;
    private LatitudeLongitudeTranslator translator = null;

    public TravelingMechanicWorldPanel(TravelingMechanicPanel travelingMechanicPanel) {
        this.travelingMechanicPanel = travelingMechanicPanel;
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                // TODO Not thread-safe during solving
                TravelingMechanicSolution solution = TravelingMechanicWorldPanel.this.travelingMechanicPanel.getSolution();
                if (solution != null) {
                    resetPanel(solution);
                }
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (translator == null) {
                    return;
                }
                double locationX = Math.round(translator.translateXToLongitude(event.getX()));
                double locationY = Math.round(translator.translateYToLatitude(event.getY()));
                TravelingMechanicSolution solution = TravelingMechanicWorldPanel.this.travelingMechanicPanel.getSolution();
                solution.getMachineComponentList().stream()
                        // Slow code, do not copy
                        .filter(element -> element.getLocationX() == locationX && element.getLocationY() == locationY)
                        .findFirst()
                        .ifPresent(travelingMechanicPanel::hurtMachineComponent);
            }
        });
    }

    public void resetPanel(TravelingMechanicSolution solution) {
        translator = new LatitudeLongitudeTranslator();
        for (MachineComponent machineComponent : solution.getMachineComponentList()) {
            translator.addCoordinates(machineComponent.getLocationY(), machineComponent.getLocationX());
        }

        Dimension size = getSize();
        double width = size.getWidth();
        double height = size.getHeight();
        translator.prepareFor(width, height);

        Graphics2D g = createCanvas(width, height);
        g.setFont(g.getFont().deriveFont((float) ATTRITION_TEXT_SIZE));
        for (MachineComponent machineComponent : solution.getMachineComponentList()) {
            int x = translator.translateLongitudeToX(machineComponent.getLocationX());
            int y = translator.translateLatitudeToY(machineComponent.getLocationY());
            double attrition = machineComponent.getAttrition();
            Color color;
            if (attrition < 0.5) {
                color = TangoColorFactory.buildPercentageColor(TangoColorFactory.CHAMELEON_1, TangoColorFactory.BUTTER_1, attrition * 2.0);
            } else {
                color = TangoColorFactory.buildPercentageColor(TangoColorFactory.BUTTER_1, TangoColorFactory.SCARLET_1, (attrition - 0.5) * 2.0);
            }
            g.setColor(color);
            g.fillRect(x + 2, y - 2, 11, 5);
            String percentage = (int) ((1.0 - attrition) * 100.0) + "%";
            g.drawString(percentage, x + 2, y - 5);
        }
        g.setColor(TangoColorFactory.CHOCOLATE_1);
        g.setFont(g.getFont().deriveFont((float) DEPARTURE_TEXT_SIZE));
        Color chocolate0 = new Color(255, 207, 132);
        for (Visit visit : solution.getVisitList()) {
            if (visit.getPrevious() != null) {
                MachineComponent previousMachineComponent = visit.getPrevious().getMachineComponent();
                MachineComponent machineComponent = visit.getMachineComponent();
                double attrition = machineComponent.getAttrition();
                g.setColor(TangoColorFactory.buildPercentageColor(chocolate0, TangoColorFactory.CHOCOLATE_3, attrition));
                g.setStroke(attrition < 0.25 ? FAT_DASHED_STROKE : attrition < 0.50 ? NORMAL_STROKE : attrition < 0.75 ? FAT_STROKE : VERY_FAT_STROKE);
                translator.drawRoute(g, previousMachineComponent.getLocationX(), previousMachineComponent.getLocationY(),
                        machineComponent.getLocationX(), machineComponent.getLocationY(), true, false);
                int x = translator.translateLongitudeToX(machineComponent.getLocationX());
                int y = translator.translateLatitudeToY(machineComponent.getLocationY());
                g.drawString(visit.getDepartureTimeMillis() + "ms", x + 2, y + 5 + DEPARTURE_TEXT_SIZE);
            }
        }
        for (Mechanic mechanic : solution.getMechanicList()) {
            MachineComponent machineComponent = mechanic.getStartMachineComponent();
            double attrition = machineComponent.getAttrition();
            g.setColor(TangoColorFactory.buildPercentageColor(chocolate0, TangoColorFactory.CHOCOLATE_3, attrition));
            g.setStroke(attrition < 0.25 ? FAT_DASHED_STROKE : attrition < 0.50 ? NORMAL_STROKE : attrition < 0.75 ? FAT_STROKE : VERY_FAT_STROKE);
            translator.drawRoute(g, mechanic.getLocationX(), mechanic.getLocationY(),
                    machineComponent.getLocationX(), machineComponent.getLocationY(), true, false);
        }
        g.setStroke(FAT_STROKE);
        g.setColor(TangoColorFactory.SKY_BLUE_1);
        for (Mechanic mechanic : solution.getMechanicList()) {
            int x = translator.translateLongitudeToX(mechanic.getLocationX());
            int y = translator.translateLatitudeToY(mechanic.getLocationY());
            g.drawLine(x - 5, y - 4, x - 5, y + 4);
            g.drawLine(x - 7, y, x - 3, y);
            g.drawLine(x - 7, y + 6, x - 5, y + 4);
            g.drawLine(x - 3, y + 6, x - 5, y + 4);
            g.drawOval(x - 7, y - 8, 4, 4);
        }
        repaint();
    }

    public void updatePanel(TravelingMechanicSolution solution) {
        resetPanel(solution);
    }

    private Graphics2D createCanvas(double width, double height) {
        int canvasWidth = (int) Math.ceil(width) + 1;
        int canvasHeight = (int) Math.ceil(height) + 1;
        canvas = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = canvas.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, canvasWidth, canvasHeight);
        return g;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvas != null) {
            g.drawImage(canvas, 0, 0, this);
        }
    }

}
