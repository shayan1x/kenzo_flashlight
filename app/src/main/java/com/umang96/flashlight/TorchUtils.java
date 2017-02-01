package com.umang96.flashlight;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;


public class TorchUtils {
	public static final int FLASH_ON = 1;
	public static final int FLASH_OFF = 0;
	
	private static final String[] ON_COMMANDS = {"echo 100 > ~/sys/class/leds/led:torch_0/brightness","echo 100 > ~/sys/class/leds/led:torch_1/brightness"};
	private static final String[] OFF_COMMANDS = {"echo 0 > ~/sys/class/leds/led:torch_0/brightness","echo 0 > ~/sys/class/leds/led:torch_1/brightness"};
	
	private static void set_flash(Context context , int status){
		switch(status){
			case TorchUtils.FLASH_OFF:
			    RunAsRoot(TorchUtils.OFF_COMMANDS , context);
			break;
			    
			case TorchUtils.FLASH_ON:
			    RunAsRoot(TorchUtils.ON_COMMANDS , context);
			break;
		}
	}

    public static boolean check(Context context, int x){
        String outp = Executor("cat ~/sys/class/leds/led:torch_0/brightness");
		int volume = 0;
        try {
			volume = Integer.parseInt(outp);
			if(!volume){
				if(x == 2)
					set_flash(TorchUtils.FLASH_ON);
				return true;
			}else{
				if(x == 2)
					set_flash(TorchUtils.FLASH_OFF);
			}
        }
        catch(Exception e)
        {
            Toast.makeText(context, "Error, Have you granted root permission ?",
                    Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static String Executor(String command) {
        StringBuffer output = new StringBuffer();
        Process p;
        try {
            p = Runtime.getRuntime().exec(new String[] { "su", "-c", command });
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;

    }

    public static void RunAsRoot(String[] cmds, Context context){
        try{
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            for (String tmpCmd : cmds) {
                os.writeBytes(tmpCmd+"\n");
            }
            os.writeBytes("exit\n");
            os.flush();
        }
        catch(Exception e)
        {
            Toast.makeText(context, "RunAsRoot failed !",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
