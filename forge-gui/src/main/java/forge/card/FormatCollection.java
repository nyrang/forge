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

import java.util.Arrays;
import java.util.List;

import forge.game.GameFormat;
import forge.util.FileSection;
import forge.util.storage.StorageReaderFileSections;
import forge.util.storage.StorageBase;

/**
 * The Class FormatUtils.
 */
public final class FormatCollection extends StorageBase<GameFormat> {



    /**
     * TODO: Write javadoc for Constructor.
     * @param io
     */
    public FormatCollection(String filename) {
        super("Format collections", new FormatReader(filename));
    }

    /**
     * Gets the standard.
     * 
     * @return the standard
     */
    public GameFormat getStandard() {
        return this.map.get("Standard");
    }

    /**
     * Gets the extended.
     * 
     * @return the extended
     */
    public GameFormat getExtended() {
        return this.map.get("Extended");
    }

    /**
     * Gets the modern.
     * 
     * @return the modern
     */
    public GameFormat getModern() {
        return this.map.get("Modern");
    }

    /** 
     * Get a specified format.
     * @return the requested format
     */
    public GameFormat getFormat(String format) {
        return this.map.get(format);
    }

    /**
     * Instantiates a new format utils.
     */
    public static class FormatReader extends StorageReaderFileSections<GameFormat> {
        public FormatReader(String file0) {
            super(file0, GameFormat.FN_GET_NAME);
        }

        @Override
        protected GameFormat read(String title, Iterable<String> body, int idx) {
            List<String> sets = null; // default: all sets allowed
            List<String> bannedCards = null; // default: nothing banned

            FileSection section = FileSection.parse(body, ":");
            String strSets = section.get("sets");
            if ( null != strSets ) {
                sets = Arrays.asList(strSets.split(", "));
            }
            String strCars = section.get("banned");
            if ( strCars != null ) {
                bannedCards = Arrays.asList(strCars.split("; "));
            }

            return new GameFormat(title, sets, bannedCards, 1 + idx);
        }
    }
}

/** 
 * TODO: Write javadoc for this type.
 *
 */