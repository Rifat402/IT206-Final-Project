public abstract class SoccerLeague {

    // ATTRIBUTES
    protected int maxTeams;
    private int numTeams = 0;
    private int matchLength;
    protected Team[] teams;
    private String matchDay;
    private int goalArea;
    private String leagueName;

    // ABSTRACT METHODS
    public abstract void addTeam(Team team);
    public abstract void dropTeam(String team);
    public abstract String predictGame(String team1, String team2);
    public abstract boolean addMatchDay(Team team1, Team team2, int goalsTeam1, int goalsTeam2);
    public abstract void endSeason();
    public abstract String createTable();
    public abstract void validateNumTeams();
    public abstract boolean hasStarted();

    // CONSTRUCTOR

    public SoccerLeague(String leagueName,int matchLength,int goalArea, String matchDay, int maxTeams) {
        setLeagueName(leagueName);
        setMatchLength(matchLength);
        setGoalArea(goalArea);
        setMatchDay(matchDay);
        setMaxTeams(maxTeams);
        this.teams = new Team[this.maxTeams];
    }

    // GETTERS
    public int getMatchLength() {return this.matchLength;}
    public String getLeagueName() {return this.leagueName;}
    public String getMatchDay() {return this.matchDay;}
    public int getGoalArea() {return this.goalArea;}
    public int getMaxTeams() {return this.maxTeams;}
    public int getNumTeams() {return this.numTeams;}
    
    // returns a new copy of the teams array
    public Team[] getTeams() {
        Team[] aTeam = new Team[this.numTeams];
        for (int i = 0; i < this.numTeams; i++){
            aTeam[i] = this.teams[i];
        }
        return aTeam;
    }

    // SETTERS

    // sets match length, checks if length is between 60 and 120 and a multiple of 10
    public void setMatchLength(int matchLength) {
        try {
            if (matchLength > 120 || matchLength < 60 || matchLength % 10 != 0){
                throw new IllegalArgumentException("Invalid match length, must be a multiple of 10 between 60 and 120 inclusive.");
            }
            this.matchLength = matchLength;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid match length, must be a multiple of 10 between 60 and 120 inclusive.");
            
        }
    }

    // sets match day to an abbreviated day of the week
    public void setMatchDay(String matchDay) {
        String[] daysOfWeek = {"m","t","w","th","f","sa","su"};
        for (int i = 0; i < daysOfWeek.length; i++){
            if (matchDay.equals(daysOfWeek[i])){
                this.matchDay = matchDay;
                return;
            }
        }
        throw new IllegalArgumentException("must select one of the following options:\nm t w th f sa su");
    }

    
    // sets goal area in meters between 10 and 60
    public void setGoalArea(int goalArea) {
        try {
            if (goalArea >= 30 || goalArea <= 1){
                throw new IllegalArgumentException("Goal Area must be an integer between 1 and 30.");
            }
            this.goalArea = goalArea;
        } catch (Exception e) {
            throw new IllegalArgumentException("Goal Area must be an integer between 1 and 30.");
            
        }
    }

    // sets max teams, used by classes directly, no input validation needed
    protected void setMaxTeams(int maxTeams){
        this.maxTeams = maxTeams;
    }

    // sets league name 
    public void setLeagueName(String leagueName){
        if (leagueName.length() < 1){
            throw new IllegalArgumentException("Must enter league name");
        }
        this.leagueName = leagueName;
    }
    
    // sets numTeams variable, only used directly by subclasses, no input validation needed
    protected void setNumTeams(int numTeams){
        this.numTeams = numTeams;
    }

    // TOSTRING
    // returns league data as string
    public String toString() {
      return String.format("League Name: %s\nNumber of teams: %d\nMatchday: %s\nGame specifications: %d minutes | %d meters squared goal area"
      ,this.leagueName,this.numTeams,this.matchDay,this.matchLength,this.goalArea);
    }

    // define num played to be overridden in subclasses if needed
    public int getNumPlayed(){
        return Integer.MIN_VALUE;
    };
}