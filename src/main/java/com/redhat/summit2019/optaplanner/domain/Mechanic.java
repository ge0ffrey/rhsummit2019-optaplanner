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

public class Mechanic extends VisitOrMechanic {

    @PlanningId
    private Long id;

    // The machine component that the mechanic is currently working on, on-route to or has just finished working on
    private MachineComponent startMachineComponent;
    // When the Mechanic will finish with working on startMachineComponent
    private Long startTimeMillis = 0L;

    private Mechanic() {
    }

    public Mechanic(Long id, MachineComponent startMachineComponent) {
        this.id = id;
        this.startMachineComponent = startMachineComponent;
    }

    @Override
    public MachineComponent getMachineComponent() {
        return startMachineComponent;
    }

    @Override
    public Long getDepartureTimeMillis() {
        return startTimeMillis;
    }

    @Override
    public String toString() {
        return "Mechanic-" + id;
    }

    // ************************************************************************
    // Getter and setters boilerplate
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public MachineComponent getStartMachineComponent() {
        return startMachineComponent;
    }

    public void setStartMachineComponent(MachineComponent startMachineComponent) {
        this.startMachineComponent = startMachineComponent;
    }

    public Long getStartTimeMillis() {
        return startTimeMillis;
    }

    public void setStartTimeMillis(Long startTimeMillis) {
        this.startTimeMillis = startTimeMillis;
    }
}
