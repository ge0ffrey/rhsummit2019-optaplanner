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

import com.redhat.summit2019.optaplanner.domain.solver.DepartureTimeUpdatingVariableListener;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

@PlanningEntity
public class Visit extends VisitOrMechanic {

    public static final long SERVICE_TIME_MILLIS = 500L;

    @PlanningId
    private Long id;
    private MachineComponent machineComponent;

    @PlanningVariable(valueRangeProviderRefs = {"mechanicRange", "visitRange"},
            graphType = PlanningVariableGraphType.CHAINED)
    private VisitOrMechanic previous;

    @AnchorShadowVariable(sourceVariableName = "previous")
    private Mechanic mechanic;
    @CustomShadowVariable(variableListenerClass = DepartureTimeUpdatingVariableListener.class,
            sources = {@PlanningVariableReference(variableName = "previous"),
                    @PlanningVariableReference(variableName = "mechanic")})
    private Long departureTimeMillis = null; // Always after Mechanic.readyTimeMillis

    private Visit() {
    }

    public Visit(Long id, MachineComponent machineComponent) {
        this.id = id;
        this.machineComponent = machineComponent;
    }

    public Long getTravelTimeMillisFromPrevious() {
        if (previous == null) {
            return null;
        }
        return previous.getMachineComponent().getTravelTimeMillisTo(machineComponent, mechanic.getSpeedInXYPerMillis());
    }

    public long getAttritionMicros() {
        return (long) (machineComponent.getAttrition() * 1_000_000.0);
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

    @Override
    public MachineComponent getMachineComponent() {
        return machineComponent;
    }

    public VisitOrMechanic getPrevious() {
        return previous;
    }

    public void setPrevious(VisitOrMechanic previous) {
        this.previous = previous;
    }

    public Mechanic getMechanic() {
        return mechanic;
    }

    public void setMechanic(Mechanic mechanic) {
        this.mechanic = mechanic;
    }

    @Override
    public Long getDepartureTimeMillis() {
        return departureTimeMillis;
    }

    public void setDepartureTimeMillis(Long departureTimeMillis) {
        this.departureTimeMillis = departureTimeMillis;
    }

}
