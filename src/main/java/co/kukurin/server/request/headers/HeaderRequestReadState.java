package co.kukurin.server.request.headers;

import static co.kukurin.server.request.HttpConstants.Ascii.CARRIAGE_RETURN;
import static co.kukurin.server.request.HttpConstants.Ascii.LINE_FEED;

enum HeaderRequestReadState {
    INITIAL,
    READ_CR,
    READ_CR_LF,
    READ_CR_LF_CR,
    READ_LF,
    READ_DOUBLE_NEWLINE;

    // TODO would be prettier as abstract HeaderReadState getNextState(int character)
    static HeaderRequestReadState determineNextState(HeaderRequestReadState currentState, int character) {
        switch(currentState) {
            case INITIAL:
                if(isCr(character)) currentState = READ_CR;
                else if(isLf(character)) currentState = READ_LF;
                break;
            case READ_CR:
                if(isLf(character)) currentState = READ_CR_LF;
                else currentState = INITIAL;
                break;
            case READ_CR_LF:
                if(isCr(character)) currentState = READ_CR_LF_CR;
                else currentState = INITIAL;
                break;
            case READ_CR_LF_CR:
                if(isLf(character)) currentState = READ_DOUBLE_NEWLINE;
                else currentState = INITIAL;
                break;
            case READ_LF:
                if(isLf(character)) currentState = READ_DOUBLE_NEWLINE;
                else currentState = INITIAL;
                break;
        }

        return currentState;
    }

    private static boolean isLf(int character) {
        return character == LINE_FEED;
    }
    private static boolean isCr(int character) {
        return character == CARRIAGE_RETURN;
    }
}
