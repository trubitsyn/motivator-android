/*
 * Copyright (C) 2016 Nikola Trubitsyn
 *
 * This file is part of Motivator.
 *
 * Motivator is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Motivator is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Motivator.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.trubitsyn.motivator.util;

import java.util.ArrayList;
import java.util.List;

import io.github.trubitsyn.motivator.model.Frequency;
import io.github.trubitsyn.motivator.model.Task;

public class MockModelFabric {

    public static Task getTask() {
        return new Task(0, "Task", System.currentTimeMillis(), Frequency.VERY_OFTEN, true);
    }

    public static List<Task> getListOfTasks() {
        List<Task> tasks = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            tasks.add(new Task(i, "Task " + i, System.currentTimeMillis(), Frequency.VERY_OFTEN, true));
        }
        return tasks;
    }
}
