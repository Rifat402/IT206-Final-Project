public class Team {
    private String teamId;
    private String teamName;
    private int points = 0;
    private int goalDiff = 0;
    private int ranking = 64;
    private double overallRating;
    private String coachName;
    private int numMatches = 0;

    // matchesPlayed stores match data for each team in a non-KO league
    // Use: Allows backtracking for league matches if an entry causes an error, may be used in the future to display team result history
    /*
    Value to be stored in each index:
        * [0]: Opponent ID (only integer portion)
        * [1]: Result / Points Earned (0 = loss, 1 = draw, 3 = win)
        * [2]: Goals For
        * [3]: Goals Against
        * [4]: Goal Difference
    */
    private int[][] matchesPlayed = new int[1000][5];

    public Team(String teamId, String teamName, double overallRating, String coachName) {
        setTeamId(teamId);
        setTeamName(teamName);
        setOverallRating(overallRating);
        setCoachName(coachName);
    }

    // Getters
    public String getTeamId() {return this.teamId;}
    public String getTeamName() {return this.teamName;}
    public double getOverallRating(){return this.overallRating;}
    public String getCoachName() {return this.coachName;}
    public int getPoints() {return this.points;}
    public int getGoalDiff(){return this.goalDiff;}
    public int getRanking(){return this.ranking;}
    public int getNumMatches(){return this.numMatches;}

    public int[][] getMatchesPlayed(){
        int[][] copy = new int[1000][5];
        for (int i = 0; i < getNumMatches(); i++){
            copy[i] = this.matchesPlayed[i];
        }
        return copy;
    }

    

    // Setters
    // validates team ID, ensures that the last 4 chars of each ID is numeric and ID is between 4-5 chars
    public void setTeamId(String teamId) {
        if (teamId.length() > 5 || teamId.length() < 4){
            throw new IllegalArgumentException("ID must be between 4-5 characters");
        }
        try{
            Integer.parseInt(teamId.substring(teamId.length() - 4));
        }
        catch (Exception e){
            throw new IllegalArgumentException("Last 4 of team ID must be numerical");
        }
        this.teamId = teamId;
    }

    
    public void setTeamName(String teamName) {
        if (teamName.length() < 1){
            throw new IllegalArgumentException("Must enter team name");
        }
        this.teamName = teamName;
    }

    public void setOverallRating(double overallRating) {
        if (overallRating > 10 || overallRating < 0){
            throw new IllegalArgumentException("Must enter value between 0-10");
        }
        this.overallRating = overallRating;
    }

    public void setCoachName(String coachName) {
        if (coachName.length() < 1){
            throw new IllegalArgumentException("Must enter coach name");
        }
        this.coachName = coachName;
    }

    public void setGoalDiff(int goalDiff){
        this.goalDiff = goalDiff;
    }

    
    public void setPoints(int points){
        this.points = points;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public void setNumMatches(int numMatches){
        this.numMatches = numMatches;
    }

    public void storeMatch(String opponentId, int gFor, int gAgainst){
        // converts last 4 digits of ID into in integer, ensures this method works for youth teams as well
        int integerizedId = Integer.parseInt(opponentId.substring(opponentId.length() - 4));

        int result;

        if (gFor == gAgainst)
            result = 1;
        else
            result = gFor > gAgainst ? 3 : 0;

        this.matchesPlayed[getNumMatches()] = new int[] {integerizedId, result, gFor, gAgainst,gFor - gAgainst};
        
        // increments the number of matches from within the method
        setNumMatches(getNumMatches() + 1);

    }
    
    public void removeMatch(int index){
        this.matchesPlayed[index] = null;
    }
}
