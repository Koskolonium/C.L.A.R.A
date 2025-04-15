package AC.Utils.CheckUtils;

import lombok.Getter;
import lombok.Setter;
import AC.Utils.CheckUtils.FastMath;
import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
public class PlayerData {

    private Long ping; // Player's current ping
    private Long pingTimestamp; // Timestamp of the current ping value

    // A list to store the last 100 positions of the player (newest position at the front)
    private LinkedList<Position> positionHistory = new LinkedList<>();

    // Timestamp to track the last time a position was added to the history
    private long lastUpdatedTimestamp = 0;

    // Lock duration (in milliseconds) between position updates, set to 5ms to throttle position updates
    private final long LOCK_DURATION = 5; // 5ms lock duration to avoid excessive updates

    // List of ping values that will be stored (only keeping the last 50 pings)
    private LinkedList<Long> pingHistory = new LinkedList<>();
    private static final int MAX_SIZE = 50; // Max number of pings to store
    private boolean isLoggingActive = false; // Flag to prevent multiple threads from logging ping
    private double lastAveragePing = 0.0; // Last calculated average ping
    private Thread pingLoggingThread = null;

    // Method to add a new position to the history list. We ensure that we do not exceed the max size of 100.
    // This also checks if 5ms have passed since the last position update to avoid overloading the system.
    public synchronized void addPosition(double x, double y, double z) {
        long currentTime = System.currentTimeMillis();

        // Check if enough time (5ms) has passed since the last position update
        if (currentTime - lastUpdatedTimestamp < LOCK_DURATION) {
            // If not enough time has passed, do nothing (lock the method for this duration)
            return;
        }

        // If enough time has passed, update the timestamp to lock the method for the next 5ms
        lastUpdatedTimestamp = currentTime;

        // Create a new position with the provided x, y, z coordinates and current timestamp
        Position newPosition = new Position(x, y, z, currentTime);

        // Add the new position to the front of the list (most recent at the front)
        positionHistory.addFirst(newPosition);

        // If the position history exceeds 100 entries, remove the oldest (last in the list)
        if (positionHistory.size() > 100) {
            positionHistory.removeLast(); // Remove the oldest position
        }
    }

    // Method to retrieve all positions that were added within the last 'durationInMillis' milliseconds
    public LinkedList<Position> getPositionsWithinTime(long durationInMillis) {
        long currentTime = System.currentTimeMillis();
        LinkedList<Position> recentPositions = new LinkedList<>();

        // Iterate through all positions to find those that are within the given time window
        for (Position position : positionHistory) {
            // If the position was added within the time frame, add it to the result list
            if (currentTime - position.getTimestamp() <= durationInMillis) {
                recentPositions.add(position);
            }
        }
        return recentPositions; // Return the list of recent positions
    }

    // Method to get a position at a specific time ago (based on ping or custom time ago)
    public Position getPositionAtTime(long timeAgo) {
        // Create a fresh copy of the position history list to prevent modifying the original list
        LinkedList<Position> positionHistoryCopy = new LinkedList<>(positionHistory);

        // Get the timestamp of the most recent position
        long latestTimestamp = positionHistoryCopy.isEmpty() ? 0 : positionHistoryCopy.getFirst().getTimestamp();

        // Calculate a target time, considering the latest position timestamp and a small adjustment factor
        long targetTime = (long) ((latestTimestamp - (timeAgo / 2)) * 1.05);

        Position exactPosition = null;
        Position earlierPosition = null;
        Position laterPosition = null;

        // Search through the position history to find a position that matches the target time
        for (Position position : positionHistoryCopy) {
            // If we find an exact match for the target time, return that position
            if (position.getTimestamp() == targetTime) {
                exactPosition = position;
                break;
            }
        }

        // If an exact match is found, return it immediately
        if (exactPosition != null) {
            return exactPosition;
        }

        // Sort the position history by timestamp (ascending order)
        positionHistoryCopy.sort((pos1, pos2) -> Long.compare(pos1.getTimestamp(), pos2.getTimestamp()));

        // Now, find the closest positions before and after the target time
        for (int i = 0; i < positionHistoryCopy.size(); i++) {
            Position currentPosition = positionHistoryCopy.get(i);
            if (currentPosition.getTimestamp() <= targetTime) {
                laterPosition = currentPosition; // This is the most recent position before or at target time
            }
            if (currentPosition.getTimestamp() > targetTime) {
                earlierPosition = currentPosition; // This is the next position after the target time
                break; // Exit the loop once the earlier position is found
            }
        }

        // If we have both earlier and later positions, interpolate between them to estimate the position at the target time
        if (earlierPosition != null && laterPosition != null) {
            // If the x and z coordinates are the same, no need to interpolate, just return the later position
            if (laterPosition.getX() == earlierPosition.getX() && laterPosition.getZ() == earlierPosition.getZ()) {
                return laterPosition;
            }

            // Calculate the time difference between the two positions
            long timeDiff = laterPosition.getTimestamp() - earlierPosition.getTimestamp();

            if (timeDiff == 0) {
                // Avoid division by zero if both timestamps are identical
                return laterPosition;
            }

            // Calculate the horizontal distance between the two positions (ignoring Y axis for simplicity)
            double deltaX = laterPosition.getX() - earlierPosition.getX();
            double deltaZ = laterPosition.getZ() - earlierPosition.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ); // Pythagorean theorem

            // Calculate the speed of movement (in blocks per millisecond)
            double speed = distance / timeDiff;

            // Calculate the time difference between the later position and the target time
            long timeToTarget = targetTime - laterPosition.getTimestamp();

            // Estimate the distance the player would have traveled from the later position to the target time
            double distanceToTarget = speed * timeToTarget;

            // Normalize the direction of movement
            double directionX = deltaX / distance;
            double directionZ = deltaZ / distance;

            // Estimate the new position at the target time based on movement speed and direction
            double estimatedX = laterPosition.getX() + directionX * distanceToTarget;
            double estimatedZ = laterPosition.getZ() + directionZ * distanceToTarget;

            // Round the estimated coordinates to 15 decimal places for precision
            estimatedX = Math.round(estimatedX * 1e15) / 1e15;
            estimatedZ = Math.round(estimatedZ * 1e15) / 1e15;
            double estimatedY = Math.round(laterPosition.getY() * 1e15) / 1e15; // Also round Y coordinate

            // Create and return the estimated position
            Position estimatedPosition = new Position(estimatedX, estimatedY, estimatedZ, targetTime);

            System.out.println("[PlayerData] Estimated position (rounded): X=" + estimatedX + ", Y=" + estimatedY + ", Z=" + estimatedZ);

            return estimatedPosition;
        }

        // If no suitable positions were found, log and return null
        System.out.println("[PlayerData] No positions available at all.");
        return null;
    }

    // Method to record the player's ping value
    public synchronized void setPing(long playerPing) {
        pingHistory.addFirst(playerPing); // Add new ping to the front of the list
        if (pingHistory.size() > MAX_SIZE) { // If the list exceeds the max size of 50
            pingHistory.removeLast(); // Remove the oldest ping value to keep the list size constant

            // Trigger the calculation and logging of the average ping
            calculateAndRepeatAverage();
        }
    }

    // Method to continuously log the average ping value every second
    private synchronized void calculateAndRepeatAverage() {
        if (pingLoggingThread != null && pingLoggingThread.isAlive()) {
            return; // Logging is already active for this player
        }
        isLoggingActive = true;
        pingLoggingThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Calculate the average ping using the FastMath utility
                    double averagePing = FastMath.getAverage(pingHistory);
                    System.out.println("Average Ping: " + Math.round(averagePing));
                    lastAveragePing = averagePing;
                    Thread.sleep(1000); // Wait for 1 second before recalculating
                } catch (InterruptedException e) {
                    // Allow thread to exit if interrupted
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        pingLoggingThread.start();
    }

    // Method to get the current average ping from the last calculated value
    public synchronized double getCurrentPing() {
        // Optionally, you can check if pingHistory is empty,
        // but if you want to return the last computed value regardless, just return lastAveragePing.
        if (pingHistory == null || pingHistory.isEmpty()) {
            return 0.0;
        }
        return lastAveragePing;
    }

    public synchronized void stopPingLogging() {
        if (pingLoggingThread != null && pingLoggingThread.isAlive()) {
            pingLoggingThread.interrupt();
        }
        pingLoggingThread = null;
        isLoggingActive = false;
    }


}