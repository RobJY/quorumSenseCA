quorumSenseCA
=============

cellular automata model of quorum sensing

To compile quorumSenseCA type:
javac quorumSenseCA.java

Usage:
+ In the far left black panel labelled 'bacteria' you can left-click the mouse to create green critters and right-click 
  to create red critters.  This panel shows the critters.
+ Press start button to start critters growing and fighting
+ Press stop button to stop critters
+ If you click with the mouse in the 'bacteria' field while simulation is running, the simulation will stop and a 
  critter will be created.  Just hit the start button to start it up again.
+ The panel labeled 'autoinducers' shows the amount of a given autoinducer in each cell.
+ The panel labeled 'attack chemicals' shows the amount of a given attack chemical in each cell.  When the autoinducer
  level reaches a threshold in a cell the critter in that cell will release attack chemical.
+ User's can set the following parameters:
    - red grow probability: this is the probability that a red critter will reproduce at each time step. (0.0-1.0)
    - green grow probability: this is the probability that a green critter will reproduce at each time step. (0.0-1.0)
    - red win probability: the probability that a red critter will win a battle with a green critter. (0.0-1.0)
    - autoinducer threshold: the quantiy of autoinducer at each cell that must be reached for a critter to start
                             making attack chemical. (0-255)
    - attack chemical threshold: amount of attack chemical that must be present in a given cell for the attack benefit
                                 and reproduction penalty to take effect. (0.0-1.0)
    - attack chemical combat benefit: amount of benefit given to attacking critter when associated attack chemical       
                                      threshold is reached. (0.0-1.0)
    - attack chemical reproduction penalty: decrease in reproduction probability for attacked critter when enemy attack
                                            chemical reaches threshold.
