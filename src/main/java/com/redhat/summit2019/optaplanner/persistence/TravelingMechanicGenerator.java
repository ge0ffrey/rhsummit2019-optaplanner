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

package com.redhat.summit2019.optaplanner.persistence;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.redhat.summit2019.optaplanner.app.TravelingMechanicApp;
import com.redhat.summit2019.optaplanner.domain.MachineComponent;
import com.redhat.summit2019.optaplanner.domain.Mechanic;
import com.redhat.summit2019.optaplanner.domain.TravelingMechanicSolution;
import com.redhat.summit2019.optaplanner.domain.Visit;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class TravelingMechanicGenerator extends LoggingMain {

    public static void main(String[] args) {
        TravelingMechanicGenerator generator = new TravelingMechanicGenerator();
        generator.writeTravelingMechanicSolution(5, 5, 1);
        generator.writeTravelingMechanicSolution(10, 10, 1);
        generator.writeTravelingMechanicSolution(10, 10, 5);
        generator.writeTravelingMechanicSolution(50, 20, 5);
        generator.writeTravelingMechanicSolution(100, 50, 10);
    }

    protected final SolutionFileIO<TravelingMechanicSolution> solutionFileIO;
    protected final File outputDir;

    public TravelingMechanicGenerator() {
        solutionFileIO = new TravelingMechanicApp().createSolutionFileIO();
        outputDir = new File(CommonApp.determineDataDir(TravelingMechanicApp.DATA_DIR_NAME), "unsolved");
    }

    private void writeTravelingMechanicSolution(int xSize, int ySize, int mechanicSize) {
        String fileName = xSize + "width-" + ySize + "height-" + mechanicSize + "mechanics";
        File outputFile = new File(outputDir, fileName + "." + solutionFileIO.getOutputFileExtension());
        TravelingMechanicSolution solution = createSolution(xSize, ySize, mechanicSize);
        solutionFileIO.write(solution, outputFile);
    }

    private TravelingMechanicSolution createSolution(int xSize, int ySize, int mechanicSize) {
        TravelingMechanicSolution solution = new TravelingMechanicSolution();

        int machineComponentSize = xSize * ySize;
        List<MachineComponent> machineComponentList = new ArrayList<>(machineComponentSize);
        long machineComponentId = 0L;
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                MachineComponent machineComponent = new MachineComponent(machineComponentId, (double) x, (double) y);
                machineComponentId++;
                machineComponentList.add(machineComponent);
            }
        }
        solution.setMachineComponentList(machineComponentList);

        List<Mechanic> mechanicList = new ArrayList<>(mechanicSize);
        long mechanicId = 0L;
        for (int i = 0; i < mechanicSize; i++) {
            MachineComponent startMachineComponent = machineComponentList.get(i * machineComponentSize / mechanicSize);
            Mechanic mechanic = new Mechanic(mechanicId, startMachineComponent);
            mechanicId++;
            mechanicList.add(mechanic);
        }
        solution.setMechanicList(mechanicList);

        List<Visit> visitList = new ArrayList<>(machineComponentList.size());
        for (MachineComponent machineComponent : machineComponentList) {
            Visit visit = new Visit(machineComponent.getId(), machineComponent);
            visitList.add(visit);
        }
        solution.setVisitList(visitList);

        return solution;
    }

}
