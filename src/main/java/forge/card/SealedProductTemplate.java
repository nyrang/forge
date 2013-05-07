/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package forge.card;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import forge.util.TextUtil;
import forge.util.storage.StorageReaderFile;

public class SealedProductTemplate {

    protected final List<Pair<String, Integer>> slots;
    protected final String name;


    public final List<Pair<String, Integer>> getSlots() {
        return slots;
    }

    public final String getEdition() {
        return name;
    }
    public SealedProductTemplate(Iterable<Pair<String, Integer>> itrSlots)
    {
        this(null, itrSlots);
    }
    
    public SealedProductTemplate(String name0, Iterable<Pair<String, Integer>> itrSlots)
    {
        slots = Lists.newArrayList(itrSlots);
        name = name0;
    }

    public int getNumberOfCardsExpected() {
        int sum = 0;
        for(Pair<String, Integer> p : slots) {
            sum += p.getRight().intValue();
        }
        return sum;
    }
    
    protected static final Function<? super SealedProductTemplate, String> FN_GET_NAME = new Function<SealedProductTemplate, String>() {
        @Override
        public String apply(SealedProductTemplate arg1) {
            return arg1.name;
        }
    };
    
    public static final class Reader extends StorageReaderFile<SealedProductTemplate> {
        public Reader(String pathname) {
            super(pathname, SealedProductTemplate.FN_GET_NAME);
        }

        @Override
        protected SealedProductTemplate read(String line, int i) {
            String[] headAndData = TextUtil.split(line, ':', 2);
            final String edition = headAndData[0];
            final String[] data = TextUtil.splitWithParenthesis(headAndData[1], ',');

            List<Pair<String, Integer>> slots = new ArrayList<Pair<String,Integer>>();
            for(String slotDesc : data) {
                String[] kv = TextUtil.splitWithParenthesis(slotDesc, ' ', 2);
                slots.add(ImmutablePair.of(kv[1], Integer.parseInt(kv[0])));
            }

            return new SealedProductTemplate(edition, slots);
        }
    }
}
