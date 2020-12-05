package gregad.eventmanager.usersservice.services.token_service;

/**
 * @author Greg Adler
 */
public interface TokenHolderService {
    void refreshToken();
    String getToken();
}
