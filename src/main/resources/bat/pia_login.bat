SET username=%1
SET password=%2
SET resource_directory=%3

echo %username%>%resource_directory%\credentials.txt
echo %password%>>%resource_directory%\credentials.txt

cd %pia_ctl_directory%
piactl login %resource_directory%\credentials.txt

del %resource_directory%\credentials.txt