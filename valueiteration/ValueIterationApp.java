

package valueiteration;

public class ValueIterationApp{

    public static final double INTENDED_PROBA = 0.8;
    public static final double LEFT_PROBA = 0.1;
    public static final double RIGHT_PROBA = 0.1;

    public static final double RMAX = 1.00;
    public static final double C = 0.10;
    public static final double EPSILON = C * RMAX;

    public static final int ROW = 6;
    public static final int COL = 6;
    public static final String GREEN_COORD = "0,0; 0,2; 0,5; 1,3; 2,4; 3,5";
    public static final String BROWN_COORD = "1,1; 1,5; 2,2; 3,3; 4,4";
    public static final String WALL_COORD = "0,1; 1,4; 4,1; 4,2; 4,3";

    public static final double WALL_REWARD = 0.00;
    public static final double WHITE_REWARD = -0.04;
    public static final double GREEN_REWARD = +1.00;
    public static final double BROWN_REWARD = -1.00;

    public static final double GAMMA = 0.99;

    public static final String[] ACTIONS = {"UP", "DOWN", "LEFT", "RIGHT"};

    public static void main(String[] arguments) {
        
        State[][] stateArr = loadStates();

        /*
        Print to see current MDP states.
        */
        printStates(stateArr);

        /*
        Value-iteration algo that returns the vector of utilities.
        */
        double[][] utilities = ValueIteration(stateArr);
        System.out.printf("Final Utitlies:\n");
        for(int r=0; r<ROW; r++){
            for (int c=0; c<COL; c++){
                System.out.printf("------");
            }
            System.out.printf("\n");
            System.out.printf("|");

            for(int c=0; c<COL; c++){
                System.out.printf("%5.01f|", utilities[r][c]);
            }
            System.out.printf("\n");
        }

        for (int c=0; c<COL; c++){
            System.out.printf("------");
        }
        System.out.printf("\n");

        /*
        Produce optimal policy for each state by using the vector of utilities.
        */
        String[][] optPolicy = getOptPolicy(utilities, stateArr);
        System.out.printf("Final Policy:\n");
        for(int r=0; r<ROW; r++){
            for (int c=0; c<COL; c++){
                System.out.printf("------");
            }
            System.out.printf("\n");
            System.out.printf("|");

            for(int c=0; c<COL; c++){
                System.out.printf("%5s|", optPolicy[r][c]);
            }
            System.out.printf("\n");
        }

        for (int c=0; c<COL; c++){
            System.out.printf("------");
        }
        System.out.printf("\n");
    }

    public static State[][] loadStates(){
        
        State[][] stateArr = new State[ROW][COL];

        /*
        Load all states as white squares first,
        because majority are white.
        */
        for(int r=0 ; r<ROW; r++) {
            for(int c=0 ; c<COL; c++) {
                stateArr[r][c] = new State(WHITE_REWARD, "WHITE");
            }
        }

        /* 
        Load wall states
        */
        String[] wallCoords = WALL_COORD.split(";");
        for(String wall : wallCoords) {
            
            wall = wall.trim();
            String [] coord = wall.split(",");
            int r = Integer.parseInt(coord[0]);
            int c = Integer.parseInt(coord[1]);
            
            stateArr[r][c] = new State(WALL_REWARD, "WALL");
        }

        /* 
        Load green states
        */
        String[] greenCoords = GREEN_COORD.split(";");
        for(String green : greenCoords) {
            
            green = green.trim();
            String [] coord = green.split(",");
            int r = Integer.parseInt(coord[0]);
            int c = Integer.parseInt(coord[1]);
            
            stateArr[r][c] = new State(GREEN_REWARD, "GREEN");
        }

        /* 
        Load brown states
        */
        String[] brownCoords = BROWN_COORD.split(";");
        for(String brown : brownCoords) {
            
            brown = brown.trim();
            String [] coord = brown.split(",");
            int r = Integer.parseInt(coord[0]);
            int c = Integer.parseInt(coord[1]);
            
            stateArr[r][c] = new State(BROWN_REWARD, "BROWN");
        }

        return stateArr;
    }

    public static void printStates(State[][] stateArr){
        System.out.println("\n");
        for(int r=0; r<ROW; r++){
            for (int c=0; c<COL; c++){
                System.out.printf("---");
            }
            System.out.printf("\n");
            System.out.printf("|");

            for(int c=0; c<COL; c++){
                if (stateArr[r][c].getType().equals("WALL")) {
                    System.out.printf("%s|", "Wa");
                } else if (stateArr[r][c].getType().equals("GREEN")) {
                    System.out.printf("%s|", "+1");
                } else if (stateArr[r][c].getType().equals("BROWN")) {
                    System.out.printf("%s|", "-1");
                } else {
                    System.out.printf("%s|", "  ");
                }
            }
            System.out.printf("\n");
        }

        for (int c=0; c<COL; c++){
            System.out.printf("---");
        }
        System.out.printf("\n");
    }

    public static double[][] ValueIteration(State[][] stateArr){
        // Initialise curU, newU and delta with zeros.
        double[][] curU = new double[ROW][COL];
        double[][] newU = new double[ROW][COL];
        double delta;
        double convergence = EPSILON * (1 - GAMMA) / GAMMA;
        double reward;
        String stateType;
        int count =0;

        do {
            for(int r=0; r<ROW; r++){
                for(int c=0; c<COL; c++){
                    curU[r][c] = newU[r][c];
                }
            }

            delta = 0.0;

            // for each state
            for(int r=0; r<ROW; r++){
                for(int c=0; c<COL; c++){
                    stateType = stateArr[r][c].getType();
                    if (stateType.equals("WALL")){
                        continue;
                    }
                    reward = stateArr[r][c].getReward();

                    newU[r][c] = reward + GAMMA * getMax(stateArr, r, c, curU);

                    if (Math.abs(newU[r][c] - curU[r][c]) > delta){
                        delta = Math.abs(newU[r][c] - curU[r][c]);
                    }
                    // System.out.printf("(%s, %s): %s\n", r, c, newU[r][c]);

                }
            }
            count++;
            System.out.printf("Iteration: %s\n", count);

        } while (!(delta < convergence));

        return curU;
    }

    public static double getMax(State[][] stateArr, int row, int col, double[][] curU){
        double max = 0;
        double sum;

        // for each action
        for(int i=0; i<ACTIONS.length; i++){
            String intendedAct = ACTIONS[i];
            int[] intendedCoord = getNewCoord(stateArr, intendedAct, row, col);

            String leftAct = _getLeftAction(intendedAct);
            int[] leftCoord = getNewCoord(stateArr, leftAct, row, col);

            String rightAct = _getRightAction(intendedAct);
            int [] rightCoord = getNewCoord(stateArr, rightAct, row, col);

            sum = INTENDED_PROBA * curU[intendedCoord[0]][intendedCoord[1]]
                    + LEFT_PROBA * curU[leftCoord[0]][leftCoord[1]]
                    + RIGHT_PROBA * curU[rightCoord[0]][rightCoord[1]];


            if (sum > max){
                max = sum;
            }
        }
        // System.out.printf("%s", max);
        // System.out.printf("\n");
        return max;
    }

    public static int[] getNewCoord(State[][] stateArr, String act, int row, int col){
        int newR;
        int newC;

        if (act.equals("UP")){
            newR = row - 1;
            newC = col;
        } else if (act.equals("DOWN")){
            newR = row + 1;
            newC = col;
        } else if (act.equals("LEFT")){
            newR = row;
            newC = col - 1;
        } else{
            newR = row;
            newC = col + 1;
        }

        try{
            if (newR >= ROW){
                newR = row;
            } else if (newR < 0){
                newR = row;
            } else if (stateArr[newR][newC].getType().equals("WALL")){
                newR = row;
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            newR = row;
        }

        try{
            if (newC >= COL){
                newC = col;
            } else if (newC < 0){
                newC = col;
            } else if (stateArr[newR][newC].getType().equals("WALL")){
                newC = col;
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            newC = col;
        }

        int[] coord = new int[2];
        coord[0] = newR;
        coord[1] = newC;

        return coord;
    }    

    public static String _getLeftAction(String intendedAct){
        if (intendedAct.equals("UP")){
            return "LEFT";
        }
        else if (intendedAct.equals("DOWN")){
            return "RIGHT";
        }
        else if (intendedAct.equals("LEFT")){
            return "DOWN";
        }
        else{
            return "UP";
        }
    }

    public static String _getRightAction(String intendedAct){
        if (intendedAct.equals("UP")){
            return "RIGHT";
        }
        else if (intendedAct.equals("DOWN")){
            return "LEFT";
        }
        else if (intendedAct.equals("LEFT")){
            return "UP";
        }
        else{
            return "DOWN";
        }
    }

    public static String[][] getOptPolicy(double[][] utilities, State[][] stateArr){
        String[][] optPolicy = new String[ROW][COL];
        double curU, upU, downU, leftU, rightU, bestU;
        String bestAct;

        for(int r=0; r<ROW; r++){
            for(int c=0; c<COL; c++){
                if (stateArr[r][c].getType().equals("WALL")){
                    continue;
                }
                curU = utilities[r][c];
                upU = getActionUtilities(utilities, stateArr, "UP", r, c);
                bestAct = "UP";
                bestU = upU;
                downU = getActionUtilities(utilities, stateArr, "DOWN", r, c);
                if (downU > bestU){
                    bestAct = "DOWN";
                    bestU = downU;
                }
                leftU = getActionUtilities(utilities, stateArr, "LEFT", r, c);
                if (leftU > bestU){
                    bestAct = "LEFT";
                    bestU = leftU;
                }
                rightU = getActionUtilities(utilities, stateArr, "RIGHT", r, c);
                if (rightU > bestU){
                    bestAct = "RIGHT";
                    bestU = rightU;
                }
                optPolicy[r][c] = bestAct;
            }
        }

        return optPolicy;
    }

    public static double getActionUtilities(double[][] utilities, State[][] stateArr, String act, int row, int col){
        int[] newCoord = getNewCoord(stateArr, act, row, col);
        int newR = newCoord[0];
        int newC = newCoord[1];
        return utilities[newR][newC];
    }
}

class State{

    public double reward;
    public String type;

    public State(double reward, String type) {
        this.reward = reward;
        this.type = type;
    }

    public double getReward(){
        return this.reward;
    }

    public void setReward(double reward) {
        this.reward = reward;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
