package net.kemitix.binder.spi;

import lombok.Builder;

import java.util.List;
import java.awt.*;
import java.awt.font.TextAttribute;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Builder
public class FontSpec {
    FontSize size;
    boolean ligatures;
    boolean kerning;

    public int size() {
        return size.getValue();
    }

    public Font derive(Font font) {
        final Map<TextAttribute, Object> attributes = new HashMap<>();
        if (ligatures) {
            attributes.put(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON);
        }
        if (kerning) {
            attributes.put(TextAttribute.KERNING, TextAttribute.KERNING_ON);
        }
        return font.deriveFont(attributes);
    }

    public String signature() {
        List<String> parts = new ArrayList<>();
        if (ligatures) {
            parts.add("lig");
        } else {
            parts.add("nlig");
        }
        if(kerning) {
            parts.add("kern");
        } else {
            parts.add("nkern");
        }
        return String.join("-", parts);
    }
}
