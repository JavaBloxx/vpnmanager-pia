package com.sid.projects;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

/**
 * @author JavaBloxx
 * @since 1.0
 */
public class PIAManager
{
    /**
     * The absolute path to the project resource folder
     */
    private static final String RESOURCE_DIRECTORY =
            Paths.get("src", "main", "resources").toFile().getAbsolutePath();

    public void changeRegion(PiaVpnRegion vpnRegion)
    {
        ProcessBuilder processBuilder = buildScriptProcess("set_region.bat", vpnRegion.toString());

        try
        {
            processBuilder.start();

            boolean newRegionConnected = false;
            while (!newRegionConnected)
            {
                if (checkConnectionStatus().equals("Connected")
                        && checkConnectionRegion().equals(vpnRegion.toString()))
                {
                    newRegionConnected = true;
                }
            }

            boolean newIpObtained = false;
            while (!newIpObtained)
            {
                if (!checkVpnIp().equals("Unknown"))
                {
                    newIpObtained = true;
                    System.out.println();
                }
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return the PIA VPN client connection status
     */
    public String checkConnectionStatus()
    {
        return startProcessAndReadFirstEcho(buildScriptProcess("get_region.bat"));
    }

    /**
     *
     * @return the PIA VPN client current region
     */
    public String checkConnectionRegion()
    {
        return startProcessAndReadFirstEcho(buildScriptProcess("get_connection_state.bat"));
    }

    /**
     *
     * @return the PIA VPN client assigned IP Address
     */
    public String checkVpnIp()
    {
        return startProcessAndReadFirstEcho(buildScriptProcess("get_vpnip.bat"));
    }


    /**
     *
     * @param fileName The name of the script to be invoked
     * @param positionalArgs Values to be passed into the script as positional args
     * @return A process builder object
     */
    private ProcessBuilder buildScriptProcess(String fileName, String... positionalArgs)
    {
        StringBuilder fileNameAndArgs = new StringBuilder(fileName);

        for (String positionalArg : positionalArgs)
            fileNameAndArgs.append(" ").append(positionalArg);

        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.directory(new File("C:\\Program Files\\Private Internet Access"));
        processBuilder.command("cmd.exe", "/c",
                RESOURCE_DIRECTORY + "\\bat\\" + fileNameAndArgs.toString());

        return processBuilder;
    }

    /**
     *
     * @param processBuilder A prepared process builder object
     * @return The first line echoed to the process console by the invoked .bat file
     */
    private String startProcessAndReadFirstEcho(ProcessBuilder processBuilder)
    {
        try
        {
            Process process = processBuilder.start();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
            return reader.readLine();
        } catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }
    }
}
