package com.github.imdmk.doublejump.notification.configuration;

import com.github.imdmk.doublejump.notification.Notification;
import com.github.imdmk.doublejump.notification.NotificationType;
import eu.okaeri.configs.schema.GenericsPair;
import eu.okaeri.configs.serdes.BidirectionalTransformer;
import eu.okaeri.configs.serdes.SerdesContext;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NotificationTransformer extends BidirectionalTransformer<Notification, String> {

    private static final String NOTIFICATION_FORMAT = "%s: %s";

    @Override
    public GenericsPair<Notification, String> getPair() {
        return this.genericsPair(Notification.class, String.class);
    }

    @Override
    public String leftToRight(@NonNull Notification notification, @NonNull SerdesContext serdesContext) {
        String type = notification.type().name().toUpperCase();
        String message = notification.message();

        return NOTIFICATION_FORMAT.formatted(type, message);
    }

    @Override
    public Notification rightToLeft(@NonNull String data, @NonNull SerdesContext serdesContext) {
        String[] arguments = data.split(":");

        NotificationType type = NotificationType.valueOf(arguments[0]);
        String message = this.replaceFirstSpace(arguments[1]);

        return new Notification(type, message);
    }

    private String replaceFirstSpace(String message) {
        if (message.startsWith(" ")) {
            return message.replaceFirst(" ", "");
        }

        return message;
    }
}