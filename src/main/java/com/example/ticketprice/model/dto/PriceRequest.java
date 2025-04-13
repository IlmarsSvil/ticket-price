package com.example.ticketprice.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(example = """
        {
          "passengers": [
            {
              "name": "John Doe",
              "age": 30,
              "terminal": "Viļņa- Lietuva",
              "bags": 1
            },
            {
              "name": "Jane Smith",
              "age": 17,
              "terminal": "Viļņa- Lietuva",
              "bags": 2
            }
          ]
        }
        """)
public class PriceRequest {

    private List<Passenger> passengers;

    public List<Passenger> getPassengers() {
        return passengers;
    }

    public void setPassengers(List<Passenger> passengers) {
        this.passengers = passengers;
    }

    public static class Passenger {
        private String name;
        private int age;
        private String terminal;
        private int bags;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getTerminal() {
            return terminal;
        }

        public void setTerminal(String terminal) {
            this.terminal = terminal;
        }

        public int getBags() {
            return bags;
        }

        public void setBags(int bags) {
            this.bags = bags;
        }
    }
}
