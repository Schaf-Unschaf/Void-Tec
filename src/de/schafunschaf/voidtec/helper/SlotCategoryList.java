package de.schafunschaf.voidtec.helper;

import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class SlotCategoryList<E> extends ArrayList<E> {

    public SlotCategoryList(@NotNull Collection<? extends E> c) {
        super(c);
    }

    @Override
    public String toString() {
        Iterator<E> iterator = iterator();
        StringBuilder stringBuilder = new StringBuilder();

        while (iterator.hasNext()) {
            E next = iterator.next();
            if (!(next instanceof SlotCategory)) {
                return "";
            }

            SlotCategory nextItem = (SlotCategory) next;
            stringBuilder.append(nextItem.getName());
            if (iterator.hasNext()) {
                stringBuilder.append(", ");
            } else {
                return stringBuilder.toString();
            }
        }
        return "";
    }
}
