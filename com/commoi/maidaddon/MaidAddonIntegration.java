package com.commoi.maidaddon;

import com.commoi.maidaddon.task.Task_Alchemist;
import com.commoi.maidaddon.task.Task_Fueling;
import com.commoi.maidaddon.task.Task_Logging;
import com.commoi.maidaddon.task.Task_Mining;
import com.commoi.maidaddon.task.Task_Organizing;
import com.commoi.maidaddon.task.Task_Water;
import com.github.tartaricacid.touhoulittlemaid.api.ILittleMaid;
import com.github.tartaricacid.touhoulittlemaid.entity.task.TaskManager;

public class MaidAddonIntegration implements ILittleMaid {
   public void addMaidTask(TaskManager manager) {
      manager.add(new Task_Mining());
      manager.add(new Task_Logging());
      manager.add(new Task_Water());
      manager.add(new Task_Alchemist());
      manager.add(new Task_Organizing());
      manager.add(new Task_Fueling());
   }
}
