/*
 * Copyright (C) 2021 Optic_Fusion1
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

package optic_fusion1.client.logging;

import jline.console.ConsoleReader;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class CustomLogger extends Logger {

    private final LogDispatcher dispatcher = new LogDispatcher(this);

    public CustomLogger(String name, String filePattern, ConsoleReader reader) {
        super(name, null);
        setLevel(Level.ALL);

        try {
            FileHandler fileHandler = new FileHandler(filePattern, 1 << 24, 8, true);
            fileHandler.setLevel(Level.parse(System.getProperty("optic_fusion1.client.file-log-level", "INFO")));
            fileHandler.setFormatter(new ConciseFormatter(false));
            addHandler(fileHandler);

            ColoredWritter consoleHandler = new ColoredWritter(reader);
            consoleHandler.setLevel(Level.parse(System.getProperty("optic_fusion1.client.console-log-level", "INFO")));
            consoleHandler.setFormatter(new ConciseFormatter(true));
            addHandler(consoleHandler);
        } catch (IOException ex) {
            System.err.println("Could not register logger!");
            ex.printStackTrace();
        }

        dispatcher.start();
    }

    @Override
    public void log(LogRecord record) {
        dispatcher.queue(record);
    }

    void doLog(LogRecord record) {
        super.log(record);
    }
}
