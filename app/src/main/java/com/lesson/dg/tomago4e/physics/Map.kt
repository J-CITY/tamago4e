package com.shooter.game.physics

import com.shooter.game.physics.physicsMath.Random

class Map {
    var chanceToStartAlive = 0.4f
    var width = 40
    var height = 100
    var map = mutableListOf<MutableList<Boolean>>()
    var deathLimit = 3
    var birthLimit = 4
    var numberOfSteps = 2

    fun initialiseMap(): MutableList<MutableList<Boolean>> {
        for(x in 0..width-1) {
            map.add(mutableListOf())
            for(y in 0..height-1) {
                if(Random(0f, 1f) < chanceToStartAlive) {
                    map[x].add(true)
                } else {
                    map[x].add(false)
                }
            }
        }
        return map
    }

    fun countAliveNeighbours(x: Int, y: Int): Int {
        var count = 0
        for (i in -1..1) {
            for(j in -1..1) {
                var neighbour_x = x+i
                var neighbour_y = y+j
                //If we're looking at the middle point
                if(i == 0 && j == 0) {
                    //Do nothing, we don't want to add ourselves in!
                } else if(neighbour_x < 0 || neighbour_y < 0 || neighbour_x >= map.size || neighbour_y >= map[0].size){
                    count = count + 1;
                } else if(map[neighbour_x][neighbour_y]){
                    count = count + 1;
                }
            }
        }
        return count
    }


    fun doSimulationStep() {
        var newMap = mutableListOf<MutableList<Boolean>>()
        //Loop over each row and column of the map
        for(x in 0..map.size-1) {
            newMap.add(mutableListOf())
            for(y in 0..map[0].size-1) {
                var nbs = countAliveNeighbours(x, y)
                //The new value is based on our simulation rules
                //First, if a cell is alive but has too few neighbours, kill it.
                if(map[x][y]) {
                    if(nbs < deathLimit){
                        newMap[x].add(false)
                    } else{
                        newMap[x].add(true)
                    }
                } else {
                    if(nbs > birthLimit) {
                        newMap[x].add(true)
                    } else{
                        newMap[x].add(false)
                    }
                }
            }
        }
        map = newMap
    }

    fun generateMap() {
        initialiseMap()
        //And now run the simulation for a set number of steps
        for (i in 0..numberOfSteps-1){
            doSimulationStep()
        }
    }

    fun Print() {
        for(x in 0..width-1) {
            for(y in 0..height-1) {
                System.out.print(""+if(map[x][y]) "#" else " ")
            }
            System.out.println("")
        }
    }
}

