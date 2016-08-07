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

    // TODO would be prettier as abstract HeaderReadState determineNext(int character)
    static HeaderRequestReadState determineNextState(HeaderRequestReadState state,
                                                     int character) {
        switch(state) {
            case INITIAL:
                if(isCr(character)) state = READ_CR;
                else if(isLf(character)) state = READ_LF;
                break;
            case READ_CR:
                if(isLf(character)) state = READ_CR_LF;
                else state = INITIAL;
                break;
            case READ_CR_LF:
                if(isCr(character)) state = READ_CR_LF_CR;
                else state = INITIAL;
                break;
            case READ_CR_LF_CR:
                if(isLf(character)) state = READ_DOUBLE_NEWLINE;
                else state = INITIAL;
                break;
            case READ_LF:
                if(isLf(character)) state = READ_DOUBLE_NEWLINE;
                else state = INITIAL;
                break;
        }

        return state;
    }

    private static boolean isLf(int character) {
        return character == LINE_FEED.getIntValue();
    }

    private static boolean isCr(int character) {
        return character == CARRIAGE_RETURN.getIntValue();
    }
}
