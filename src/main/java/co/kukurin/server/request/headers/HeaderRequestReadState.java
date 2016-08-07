package co.kukurin.server.request.headers;

import static co.kukurin.server.request.HttpConstants.Ascii.CR;
import static co.kukurin.server.request.HttpConstants.Ascii.LF;

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
                if(character == CR.getIntValue()) state = READ_CR;
                else if(character == LF.getIntValue()) state = READ_LF;
                break;
            case READ_CR:
                if(character == LF.getIntValue()) state = READ_CR_LF;
                else state = INITIAL;
                break;
            case READ_CR_LF:
                if(character == CR.getIntValue()) state = READ_CR_LF_CR;
                else state = INITIAL;
                break;
            case READ_CR_LF_CR:
                if(character == LF.getIntValue()) state = READ_DOUBLE_NEWLINE;
                else state = INITIAL;
                break;
            case READ_LF:
                if(character == LF.getIntValue()) state = READ_DOUBLE_NEWLINE;
                else state = INITIAL;
                break;
        }

        return state;
    }
}
