#
WORK IN PROGRESS

This program can log entries from SmartDashboard and save them as a JSON files to be viewed later on the Wildlog viewer.

Open the logger with VSCode(wpi version) and then run as simulation with F5. 
Program will start recording once it sees a SmartDashboard entry 'Logger' change to true.  It will stop logging 
and save create/save the log file once this boolean changes to false or if it is disconnected from NetworkTables.
The robot code currently turns Logger=true in Auton and Teleop inits and turns Logger=false in Auton and Teleop disable.

Logger will load the list of headings to record from C:\Users\Public\Documents\LOGGER\default_list.
An example default_list is provided on this github.  One line per heading with no commas or other separators.

Headings must be exactly as entered into SmartDashboard.  
If entry is part of a table, then for each entry in the table, include the table name then "/" and then heading name.  i.e. Velocity/0

Logs will be saved to C:\Users\Public\Documents\Logs (as well as synced to Google Drive Folder if running on my laptop).
File names are:  log_month-day_hour-minute-second_eventName_mN matchNumber_mT matchType_rN replayNumber_alliance_sN stationNumber
           i.e.  log_4-1_20-19-46__mN0.0_mT0.0_rN0.0_Blue_sN2.0.txt

Logs are viewed using WildLog viewer written by the FRC 111 Wildstang team. (java executable provided on this github)
Batch file also provided to launch viewer.
In order to run:
  Install Java jdk1.8.0_281
  copy wildlogviewer to \bin directory of that Java installation (i.e. \Program Files\Java\jdk1.8.0_281\bin
  use .bat file to run
