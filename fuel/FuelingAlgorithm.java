package com.commoi.maidaddon.task.fuel;

public enum FuelingAlgorithm {
   COST_EFFICIENCY("Cost Efficiency"),
   BEST_FUEL("Best Fuel"),
   WORST_FUEL("Worst Fuel");

   private final String name;

   private FuelingAlgorithm(String name) {
      this.name = name;
   }

   public String toString() {
      return this.name;
   }

   // $FF: synthetic method
   private static FuelingAlgorithm[] $values() {
      return new FuelingAlgorithm[]{COST_EFFICIENCY, BEST_FUEL, WORST_FUEL};
   }
}
