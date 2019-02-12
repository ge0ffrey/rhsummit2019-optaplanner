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

package com.redhat.summit2019.optaplanner.domain;

import org.optaplanner.core.api.domain.lookup.PlanningId;

public class MachineComponent {

    public static final double SPEED_IN_XY_PER_MILLIS = 0.1;

    @PlanningId
    private Long id;

    private double locationX;
    private double locationY;

    private double attrition = 0.0; // 0.0 = perfect, 1.0 = broken

    public MachineComponent(Long id, double locationX, double locationY) {
        this.id = id;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    public long getTravelTimeMillisTo(MachineComponent machineComponent) {
        // Euclidean distance (Pythagorean theorem)
        double xDifference = machineComponent.locationX - locationX;
        double yDifference = machineComponent.locationY - locationY;
        double distance = Math.sqrt((xDifference * xDifference) + (yDifference * yDifference));
        // Floored to long in millis (3 digits) to avoid score corruption
        return (long) (distance / SPEED_IN_XY_PER_MILLIS);
    }

    @Override
    public String toString() {
        return "MachineComponent-" + id;
    }

    // ************************************************************************
    // Getter and setters boilerplate
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public double getLocationX() {
        return locationX;
    }

    public double getLocationY() {
        return locationY;
    }

    public double getAttrition() {
        return attrition;
    }

    public void setAttrition(double attrition) {
        this.attrition = attrition;
    }

}
