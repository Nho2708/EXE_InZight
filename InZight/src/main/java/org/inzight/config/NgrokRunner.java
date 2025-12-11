package org.inzight.config;

import java.io.File;

public class NgrokRunner {

    //Cấu hình domain & đường dẫn ngrok
//    private static final String NGROK_PATH = "C:\\ngrok\\ngrok.exe";
    private static final String NGROK_PATH = "C:\\Program Files\\ngrok-v3\\ngrok.exe";
    private static final String NGROK_DOMAIN = "maynard-unphysical-planographically.ngrok-free.dev";
    private static final int LOCAL_PORT = 8080;

    public static void startNgrok() {
        try {
            File ngrokFile = new File(NGROK_PATH);
            if (!ngrokFile.exists()) {
                System.err.println("Không tìm thấy file ngrok tại: " + NGROK_PATH);
                return;
            }

            String[] command = {
                    "cmd.exe", "/c",
                    "start \"Ngrok Tunnel\" \"" + NGROK_PATH + "\" http --domain=" + NGROK_DOMAIN + " " + LOCAL_PORT
            };

            new ProcessBuilder(command).start();
            System.out.println("Ngrok tunnel started on https://" + NGROK_DOMAIN);

        } catch (Exception e) {
            System.err.println("Lỗi khi chạy ngrok: " + e.getMessage());
        }
    }

}
