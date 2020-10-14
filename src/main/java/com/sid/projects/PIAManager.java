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
    public PIAManager()
    {
        startProcessAndReadFirstEcho(buildScriptProcess("pia_background.bat", "enable"));
    }

    /**
     * The absolute path to the project resource folder
     */
    private static final String RESOURCE_DIRECTORY =
            Paths.get("src", "main", "resources").toFile().getAbsolutePath();

    /**
     * The absolute path to the folder containing the PIA CLI executable
     */
    private static final String PIA_CTL_DIRECTORY = "C:\\Program Files\\Private Internet Access";

    /**
     *
     * @param vpnRegion
     * @return
     */
    public String swapConnectedRegions(PiaVpnRegion vpnRegion)
    {
        if (!checkConnectionStatus().equals("Connected")) return "Not connected";

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
                }
            }

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        return "Connected";
    }

    public String changeRegion(PiaVpnRegion region)
    {
        return startProcessAndReadFirstEcho(buildScriptProcess("set_region.bat", region.toString()));
    }

    /**
     *
     */
    public String connect(PiaVpnRegion region)
    {
        String connectionStatus = checkConnectionStatus();
        if (connectionStatus.equals("Connected")
                || connectionStatus.equals("Connecting")
                || connectionStatus.equals("Reconnecting"))
        {
            disconnect();
        }

        if (!checkConnectionRegion().equals(region.toString()))
        {
            changeRegion(region);
        }

        ProcessBuilder processBuilder = buildScriptProcess("pia_connect.bat");

        try
        {
            processBuilder.start();

            boolean newRegionConnected = false;
            while (!newRegionConnected)
            {
                System.out.println("Looping Status");
                if (checkConnectionStatus().equals("Connected"))
                {
                    newRegionConnected = true;
                }
            }

            boolean newIpObtained = false;
            while (!newIpObtained)
            {
                System.out.println("Looping IP");
                if (!checkVpnIp().equals("Unknown"))
                {
                    newIpObtained = true;
                }
            }

        } catch (IOException e)
        {
            e.printStackTrace();
            return "Connection Failed";
        }
        return "Connected";
    }

    /**
     *
     */
    public void disconnect()
    {
        startProcessAndReadFirstEcho(buildScriptProcess("pia_disconnect.bat"));
    }

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public String login(String username, String password)
    {
        String result = startProcessAndReadFirstEcho(buildScriptProcess("pia_login.bat", username, password,
                RESOURCE_DIRECTORY));
        if (result == null || result.isEmpty())
        {
            return "Logged in";
        } else
        {
            return result;
        }
    }

    /**
     *
     */
    public void logout()
    {
        startProcessAndReadFirstEcho(buildScriptProcess("pia_logout.bat"));
    }

    /**
     *
     * @return the PIA VPN client connection status
     */
    public String checkConnectionStatus()
    {
        return startProcessAndReadFirstEcho(buildScriptProcess("pia_get.bat", "connectionstate"));
    }

    /**
     *
     * @return the PIA VPN client current region
     */
    public String checkConnectionRegion()
    {
        return startProcessAndReadFirstEcho(buildScriptProcess("pia_get.bat", "region"));
    }

    /**
     *
     * @return the PIA VPN client assigned IP Address
     */
    public String checkVpnIp()
    {
        return startProcessAndReadFirstEcho(buildScriptProcess("pia_get.bat", "vpnip"));
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
        processBuilder.directory(new File(PIA_CTL_DIRECTORY));
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
