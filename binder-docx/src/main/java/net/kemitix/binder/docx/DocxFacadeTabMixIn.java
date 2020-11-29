package net.kemitix.binder.docx;

import org.docx4j.wml.CTTabStop;
import org.docx4j.wml.PPrBase;
import org.docx4j.wml.R;
import org.docx4j.wml.STTabJc;
import org.docx4j.wml.Tabs;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.Arrays;

public interface DocxFacadeTabMixIn
        extends DocxFacadeMixIn {

    default PPrBase.Ind tabIndent(
            @Nullable Integer left,
            @Nullable Integer right,
            int hanging
    ) {
        PPrBase.Ind ind = factory().createPPrBaseInd();
        if (left != null) ind.setLeft(BigInteger.valueOf(left));
        if (right != null) ind.setRight(BigInteger.valueOf(right));
        ind.setHanging(BigInteger.valueOf(hanging));
        return ind;
    }

    default Tabs tabs(CTTabStop[] positions) {
        Tabs tabs = factory().createTabs();
        tabs.getTab().addAll(Arrays.asList(positions));
        return tabs;
    }

    default CTTabStop tabLeft(int position) {
        CTTabStop tabStop = factory().createCTTabStop();
        tabStop.setPos(BigInteger.valueOf(position));
        tabStop.setVal(STTabJc.LEFT);
        return tabStop;
    }

    default CTTabStop tabRight(int position) {
        CTTabStop tabStop = factory().createCTTabStop();
        tabStop.setPos(BigInteger.valueOf(position));
        tabStop.setVal(STTabJc.RIGHT);
        return tabStop;
    }

    default R.Tab tab() {
        return factory().createRTab();
    }

}
