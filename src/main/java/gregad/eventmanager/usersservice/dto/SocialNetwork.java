package gregad.eventmanager.usersservice.dto;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * @author Greg Adler
 */
@RequiredArgsConstructor
public enum SocialNetwork {
    FACEBOOK("facebook","http://facebook-connector-service/users"),
    TWITTER("twitter","http://twitter-connector-service/users"),
    INSTAGRAM("instagram","http://instagram-connector-service/users");
    private final String network;
    private final String url;

    public static String getNetwork(String str){
        SocialNetwork socialNetworkConstants = Arrays
                .stream(values())
                .filter(e -> e.network.equalsIgnoreCase(str))
                .findAny()
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("Network: " + str + " not supported yet");
                });
        return socialNetworkConstants.network;
    }

    public static String getUrl(String str){
        SocialNetwork socialNetworkConstants = Arrays
                .stream(values())
                .filter(e -> e.network.equalsIgnoreCase(str))
                .findAny()
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("Network: " + str + " not supported yet");
                });
        return socialNetworkConstants.url;
    }
    
}
