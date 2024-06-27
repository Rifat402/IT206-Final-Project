import javax.swing.JOptionPane;
public class SoccerManagement {
    // array to stores instance of each league type ([0]: league, [1]: Knockout, [2]: Youth League)
    public static SoccerLeague[] leagues = {null,null,null};
    public static void main(String[] args) 
    {
        boolean exited = false;
        int leagueToModify;
        int option;

        promptAddLeagues();

        if (leagues[0] == null && leagues[1] == null && leagues[2] == null)
        {
            exited = true;
            JOptionPane.showMessageDialog(null, "Thank you for using the soccer league management system.");
            return;
        }
        do
        {
            try 
            {
                leagueToModify = Integer.parseInt(JOptionPane.showInputDialog("Enter league to modify\n1) League\n2) Knockout\n3) Youth League")) - 1;
                if (leagues[leagueToModify] == null)
                {
                    throw new IllegalArgumentException("League not created");
                }
                if (leagueToModify == 2 && leagues[0] == null)
                {
                    throw new IllegalArgumentException("Cannot add youth league without existing league.");
                }
                option = Integer.parseInt(JOptionPane.showInputDialog("Enter Option: \n1) Add Team\n2) Drop Team \n3) Predict Game \n4) Add Matchday \n5) End Season \n6) Show Table \n7) Exit \n8) Add League"));
                switch (option) 
                {
                    case 1:
                        promptAddTeam(leagueToModify);
                        break;
                    case 2:
                        promptDropTeam(leagueToModify);
                        break;
                    case 3:
                        promptPredictGame(leagueToModify);
                        break;
                    case 4:
                        promptAddMatchDay(leagueToModify);
                        break;
                    case 5:
                        promptEndSeason(leagueToModify);
                        break;
                    case 6:
                        promptShowTable(leagueToModify);
                        break;
                    case 7:
                        JOptionPane.showMessageDialog(null,"Thank you for using the soccer league management system.");
                        exited = true;
                        break;
                    case 8:
                        promptAddLeagues();
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "Invalid Selection");
                }
                
            } 

            catch (Exception e) 
            {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        }

        while(!exited);

    }

    // Allows user to enter all of the leagues they want to manage
    public static void promptAddLeagues()
    {
        boolean isDone = false;
        int leagueType = -1;
        int matchLength = -1;
        String matchDay = "";
        int goalArea = 0;
        String leagueName = "";
        String ageRange = "";

        do
        {
            try 
            {
                // get type of league to add (-1 at the end to map value to an index in the array)
                leagueType = Integer.parseInt( JOptionPane.showInputDialog("Select League to enter\n1) League\n2) Knockout\n3) Youth League\n4) Continue\n\nNOTE: Only one of each league type may be added.") ) - 1;

                // exit loop when use chooses "Continue"
                if (leagueType == 3) return;
                
            } 

            catch (Exception e) 
            {
                JOptionPane.showMessageDialog(null, "Invalid argument, enter the integer corresponding to the options.");
            }

            try 
            {
                matchLength = Integer.parseInt(JOptionPane.showInputDialog("Enter Match length in minutes between 60 and 120\nmust be a multiple of 10"));
                matchDay = JOptionPane.showInputDialog(null, "Enter one of the options for a matchday:\nm t w th f sa su");
                goalArea = Integer.parseInt(JOptionPane.showInputDialog("Enter goal area betweeen 1 and 30"));
                leagueName = JOptionPane.showInputDialog("Enter League Name"); 
                
            } 
            
            catch (Exception e) 
            {
                JOptionPane.showMessageDialog(null, "Invalid input");
            }
           try
           {
                // leaguetype is incremented to map cases to provided options
                switch (leagueType + 1) 
                {
                    case 1:
                        leagues[leagueType] = new League(matchLength, matchDay, goalArea, leagueName);
                        break;
                    case 2:
                        leagues[leagueType] = new Knockout(matchLength, matchDay, goalArea, leagueName);
                        break;
                    case 3:
                        if (leagues[0] == null)
                            throw new IllegalArgumentException("Error!\nSoccer League must be initialized before Youth League");
                        
                        ageRange = JOptionPane.showInputDialog("Enter Age Range: ");
                        leagues[leagueType] = new YouthLeague(matchLength, matchDay, goalArea, leagueName, ageRange);
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid option.");
                }
            }

            catch(Exception e)
            {
                JOptionPane.showMessageDialog(null, "Invalid Option");
            }
        }

        while(!isDone);
    }

    public static void promptAddTeam(int leagueToModify)
    {

        String teamId = "";
        String teamName = "";
        double overallRating = 0;
        String coachName = "";


        try 
        {
            teamId = JOptionPane.showInputDialog("Enter Team ID\nAdult Team Format: xxxx\nYouth Team Format: Yxxxx\n x = Positive Integer");
            teamName = JOptionPane.showInputDialog("Enter team name");
            overallRating = Double.parseDouble(JOptionPane.showInputDialog("Enter Overall rating between 1-10"));
            coachName = JOptionPane.showInputDialog("Enter coach name");
            leagues[leagueToModify].addTeam(new Team(teamId, teamName, overallRating, coachName));
            JOptionPane.showMessageDialog(null, "Team Added Successfully");
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void promptDropTeam(int leagueToModify)
    {
        if (leagues[leagueToModify].hasStarted())
        {
            JOptionPane.showMessageDialog(null, "Cannot remove teams in the middle of the tournament.");
            return;
        }
        try 
        {
            String teamId = JOptionPane.showInputDialog("Enter ID of team to drop");
            leagues[leagueToModify].dropTeam(teamId);
            JOptionPane.showMessageDialog(null,"Team Dropped Successfully");
            
        } 
        catch (Exception e) 
        {
            JOptionPane.showMessageDialog(null,e.getMessage());      
        }
    }
    
    public static void promptPredictGame(int leagueToModify)
    {
       String home = JOptionPane.showInputDialog("Enter home team ID: ");
       String away = JOptionPane.showInputDialog("Enter away team ID: ");
       try
       {
        JOptionPane.showMessageDialog(null,leagues[leagueToModify].predictGame(home,away));
       }
       catch (Exception e)
       {
        JOptionPane.showMessageDialog(null, e.getMessage());
       }
    }

    public static void promptAddMatchDay(int leagueToModify){  
        // first ensure the number of teams is valid
        leagues[leagueToModify].validateNumTeams();
        boolean isDone = false;
        
        int goals1 = 0;
        int goals2 = 0;

        // FUNTIONALITY FOR NON KNOCKOUT LEAGUES
        if (leagueToModify != 1)
        {
            while (!isDone)
            {
                // Ensure team exists
                String team1 = JOptionPane.showInputDialog("Enter Home Team ID: ");
                String team2 = JOptionPane.showInputDialog("Enter Away Team ID: ");            
                // stores teams to modify
                Team homeTeam = findTeamById(leagueToModify, team1);
                Team awayTeam = findTeamById(leagueToModify, team2);
               
                if (homeTeam == null || awayTeam == null)
                {
                    JOptionPane.showMessageDialog(null, "One of the entered teams are invalid");
                    return;
                }
                // get scoreline for game
                try
                {
                    goals1 = Integer.parseInt(JOptionPane.showInputDialog("Enter goals scored by home: "));
                    goals2 = Integer.parseInt(JOptionPane.showInputDialog("Enter goals scored by away: "));
                }
    
                catch(Exception e)
                {
                    JOptionPane.showMessageDialog(null,"Invalid Input");
                }
    
                isDone = leagues[leagueToModify].addMatchDay(homeTeam, awayTeam, goals1, goals2);
            }
            JOptionPane.showMessageDialog(null, "Matchday entered");
            return;
        }


        // KNOCKOUT LOGIC
        String team1 = JOptionPane.showInputDialog("Enter Home Team ID: ");
        String team2 = JOptionPane.showInputDialog("Enter Away Team ID: ");            
        // stores teams to modify
        Team homeTeam = findTeamById(leagueToModify, team1);
        Team awayTeam = findTeamById(leagueToModify, team2);
        
        if (homeTeam == null || awayTeam == null)
        {
            JOptionPane.showMessageDialog(null, "One of the entered teams are invalid");
            return;
        }
        // get scoreline for game
        try
        {
            goals1 = Integer.parseInt(JOptionPane.showInputDialog("Enter goals scored by home: "));
            goals2 = Integer.parseInt(JOptionPane.showInputDialog("Enter goals scored by away: "));
        }

        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null,"Invalid Input");
        }

        leagues[leagueToModify].addMatchDay(homeTeam, awayTeam, goals1, goals2);
        
        JOptionPane.showMessageDialog(null, "Matchday entered");
    }

    public static void promptEndSeason(int leagueToModify){
        try
        {
            leagues[leagueToModify].endSeason();
            JOptionPane.showMessageDialog(null, "Season ended and values reset");
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public static void promptShowTable(int leagueToModify)
    {
        String output = leagues[leagueToModify].createTable();
        JOptionPane.showMessageDialog(null, output);
    }


    // utility method to find team in array
    public static Team findTeamById(int leagueToModify, String teamId)
    {
        Team[] teamsArray = leagues[leagueToModify].getTeams();
        for (int i = 0; i < teamsArray.length; i++)
        {
            if (teamsArray[i]== null)
                continue;
            
            else if (teamsArray[i].getTeamId().equals(teamId))
                return teamsArray[i];
            
        }
        return null;
    }
}