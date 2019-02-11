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

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.drools.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

@PlanningSolution
public class TravelingMechanicSolution {

    @ProblemFactCollectionProperty
    private List<MachineComponent> machineComponentList;

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "mechanicRange")
    private List<Mechanic> mechanicList;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "visitRange")
    private List<Visit> visitList;

    @PlanningScore
    private HardSoftLongScore score;


    // ************************************************************************
    // Getter and setters boilerplate
    // ************************************************************************

    public List<MachineComponent> getMachineComponentList() {
        return machineComponentList;
    }

    public void setMachineComponentList(List<MachineComponent> machineComponentList) {
        this.machineComponentList = machineComponentList;
    }

    public List<Mechanic> getMechanicList() {
        return mechanicList;
    }

    public void setMechanicList(List<Mechanic> mechanicList) {
        this.mechanicList = mechanicList;
    }

    public List<Visit> getVisitList() {
        return visitList;
    }

    public void setVisitList(List<Visit> visitList) {
        this.visitList = visitList;
    }

    public HardSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

}
