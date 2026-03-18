package com.partharoypc.adglide.util;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.partharoypc.adglide.AdGlide;
import com.partharoypc.adglide.AdGlideConfig;
import com.partharoypc.adglide.R;
import java.util.List;

/**
 * A secret activity to debug SDK configuration and ad fill status.
 */
public class DebugActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adglide_debug_hud);

        refreshUI();
        findViewById(R.id.refresh_btn).setOnClickListener(v -> refreshUI());
        findViewById(R.id.clear_btn).setOnClickListener(v -> {
            PerformanceLogger.clear();
            refreshUI();
        });
        findViewById(R.id.btn_close_hud).setOnClickListener(v -> finish());
        findViewById(R.id.btn_test_ad).setOnClickListener(v -> {
            AdGlideConfig config = AdGlide.getConfig();
            if (config != null) {
                AdGlide.showInterstitial(this, null);
            }
        });
    }

    @SuppressWarnings("SetTextI18n")
    private void refreshUI() {
        TextView configTv = findViewById(R.id.config_text);
        TextView logsTv = findViewById(R.id.logs_text);
        TextView waterfallTv = findViewById(R.id.waterfall_text);

        AdGlideConfig config = AdGlide.getConfig();
        if (config != null) {
            String configInfo = "Status: " + (config.getAdStatus() ? "ENABLED" : "DISABLED") + "\n" +
                    "Primary: " + config.getPrimaryNetwork() + "\n" +
                    "Backups: " + config.getBackupNetworks().toString() + "\n" +
                    "Test Mode: " + config.isTestMode() + "\n" +
                    "GDPR: " + config.isEnableGDPR() + "\n" +
                    "Interval (Int): " + config.getInterstitialInterval() + "\n" +
                    "Interval (Rew): " + config.getRewardedInterval();
            configTv.setText(configInfo);
        } else {
            configTv.setText("AdGlide not initialized.");
        }

        List<PerformanceLogger.LogEntry> logs = PerformanceLogger.getLogs();
        if (waterfallTv != null) {
            StringBuilder waterfall = new StringBuilder();
            for (PerformanceLogger.LogEntry entry : logs) {
                if (entry.message.contains("loaded") || entry.message.contains("failed") || entry.message.contains("showed")) {
                    String marker = entry.level.equals("ERROR") ? "✗" : "✓";
                    waterfall.append(marker).append(" [").append(entry.category).append("] ")
                            .append(entry.message).append("\n");
                }
            }
            waterfallTv.setText(waterfall.length() > 0 ? waterfall.toString() : "— No ad requests yet —");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = logs.size() - 1; i >= 0; i--) {
            PerformanceLogger.LogEntry entry = logs.get(i);
            String color = entry.level.equals("ERROR") ? "!!!" : entry.level.equals("WARN") ? "???" : ">>>";
            sb.append("[").append(entry.category).append("] ").append(color).append(" ").append(entry.message)
                    .append("\n\n");
        }
        logsTv.setText(sb.length() > 0 ? sb.toString() : "— No logs yet —");
    }
}
