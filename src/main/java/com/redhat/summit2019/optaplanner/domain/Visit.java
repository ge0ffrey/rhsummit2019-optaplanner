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

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;

@PlanningEntity
public class Visit extends VisitOrMechanic {

    @PlanningId
    private Long id;
    private MachineComponent machineComponent;

    @PlanningVariable(valueRangeProviderRefs = {"mechanicRange", "visitRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    private VisitOrMechanic previous;

    private Visit() {
    }

    public Visit(Long id, MachineComponent machineComponent) {
        this.id = id;
        this.machineComponent = machineComponent;
    }

    @Override
    public String toString() {
        return "Visit-" + id;
    }

    // ************************************************************************
    // Getter and setters boilerplate
    // ************************************************************************

    public Long getId() {
        return id;
    }

    public MachineComponent getMachineComponent() {
        return machineComponent;
    }

    public VisitOrMechanic getPrevious() {
        return previous;
    }

    public void setPrevious(VisitOrMechanic previous) {
        this.previous = previous;
    }

}
