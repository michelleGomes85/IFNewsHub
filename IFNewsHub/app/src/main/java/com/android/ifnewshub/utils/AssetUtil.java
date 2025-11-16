package com.android.ifnewshub.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetUtil {
    public static String readAsset(Context c, String file) {

        try {
            InputStream is = c.getAssets().open(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null)
                sb.append(line);

            br.close();

            return sb.toString();
        } catch (Exception e) {
            return "<h2>Erro ao carregar HTML</h2>";
        }
    }
}
