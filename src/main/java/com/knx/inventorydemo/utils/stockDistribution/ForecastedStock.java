package com.knx.inventorydemo.utils.stockDistribution;

public class ForecastedStock implements Comparable<ForecastedStock>{

        private String productId;
        private String relativeId;
        private float Quantity;
        private int priorityScore;

        public String getProductId() {
            return productId;
        }
       
        public String getRelativeId() {
            return relativeId;
        }


        public float getQuantity() {
            return Quantity;
        }

        public int getPriorityScore() {
            return priorityScore;
        }

        @Override
        public int compareTo(ForecastedStock o) {
            return Integer.compare(this.priorityScore, o.getPriorityScore());
        }


    }