package com.kosmx.emotes.executor.emotePlayer;

import com.kosmx.emotes.common.emote.EmoteData;
import com.kosmx.emotes.common.tools.Vec3d;

import javax.annotation.Nullable;
import java.util.UUID;

//Every player will be IEmotePlayer
public interface IEmotePlayerEntity<T extends IEmotePlayer > {

    void playEmote(EmoteData emote);

    @Nullable
    T getEmote();

    boolean isPlayingEmote();

    void stopEmote();

    UUID getUUID();

    boolean isMainPlayer();

    boolean isNotStanding();

    Vec3d emotesGetPos();

    Vec3d getPrevPos();

    float getBodyYaw();
    float getViewYaw();
    void setBodyYaw(float newYaw);

    void emoteTick();

    default void emoteTickCallback(){}
    default void emoteStartPlayCallback(){}
}