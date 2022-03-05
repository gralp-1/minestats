package com.srmworks.minestats;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class StyleText {
    public static Component linkStyle(String text, String url) {
        return new Component() {
            @Override
            public Style getStyle() {
                return Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)).withColor(ChatFormatting.AQUA);
            }

            @Override
            public String getContents() {
                return text;
            }

            @Override
            public List<Component> getSiblings() {
                return null;
            }

            @Override
            public MutableComponent plainCopy() {
                return null;
            }

            @Override
            public MutableComponent copy() {
                return null;
            }

            @Override
            public FormattedCharSequence getVisualOrderText() {
                return null;
            }
        };
    }
}
