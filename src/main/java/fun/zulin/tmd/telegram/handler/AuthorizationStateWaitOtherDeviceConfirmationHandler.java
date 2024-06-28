package fun.zulin.tmd.telegram.handler;

import it.tdlight.client.*;
import it.tdlight.jni.TdApi;

public final class AuthorizationStateWaitOtherDeviceConfirmationHandler implements
        GenericUpdateHandler<TdApi.UpdateAuthorizationState> {

    private final ClientInteraction clientInteraction;

    public AuthorizationStateWaitOtherDeviceConfirmationHandler(ClientInteraction clientInteraction) {
        this.clientInteraction = clientInteraction;
    }

    @Override
    public void onUpdate(TdApi.UpdateAuthorizationState update) {
        if (update.authorizationState.getConstructor() == TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR) {
            TdApi.AuthorizationStateWaitOtherDeviceConfirmation authorizationState
                    = (TdApi.AuthorizationStateWaitOtherDeviceConfirmation) update.authorizationState;
            ParameterInfo parameterInfo = new ParameterInfoNotifyLink(authorizationState.link);
            clientInteraction.onParameterRequest(InputParameter.NOTIFY_LINK, parameterInfo).whenComplete((ignored, ex) -> {
                if (ex != null) {
                    System.out.println(ex.getMessage());
                }
            });
        }
    }
}
