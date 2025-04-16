package AC.Utils.CheckUtils;

import lombok.Getter;
import lombok.Setter;
import java.util.LinkedList;

@Setter
@Getter
public class PlayerData {


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
        if (positionHistoryCopy.isEmpty()) {
            System.out.println("[DEBUG] Position history is empty, returning null.");
            return null;
        }
        System.out.println("[PlayerData] Position History:");
        for (Position position : positionHistoryCopy) {
            System.out.println(" - Position{x=" + position.getX() + ", y=" + position.getY() +
                    ", z=" + position.getZ() + ", timestamp=" + position.getTimestamp() + "}");
        }
        // Find the latest timestamp in the position history
        long latestTimestamp = positionHistoryCopy.getFirst().getTimestamp();

        // Updated target time calculation
        long candidateTime = latestTimestamp - (timeAgo / 2);
        long targetTime = candidateTime >= 50 ? candidateTime : (latestTimestamp - 50);

        System.out.println("[DEBUG] Latest Timestamp: " + latestTimestamp);
        System.out.println("[DEBUG] Target Time: " + targetTime);

        Position exactPosition = null;
        Position earlierPosition = null;
        Position laterPosition = null;

        // Iterate to find closest positions
        for (Position currentPosition : positionHistoryCopy) {
            if (currentPosition.getTimestamp() == targetTime) {
                System.out.println("[DEBUG] Found exact match at timestamp=" + currentPosition.getTimestamp());
                return currentPosition; // Return immediately if we have an exact match
            }
            if (currentPosition.getTimestamp() <= targetTime) {
                laterPosition = currentPosition;
            }
            if (currentPosition.getTimestamp() > targetTime) {
                earlierPosition = currentPosition;
                break; // Stop after finding the first earlierPosition
            }
        }

        // If there's an earlier position but no later position, default to earlier position
        if (laterPosition == null && earlierPosition != null) {
            System.out.println("[DEBUG] No later position found, returning earlier position.");
            return earlierPosition;
        }

        System.out.println("[DEBUG] Later Position: " + (laterPosition != null ? laterPosition.getTimestamp() : "null"));
        System.out.println("[DEBUG] Earlier Position: " + (earlierPosition != null ? earlierPosition.getTimestamp() : "null"));

        // Sort the position history by timestamp (ascending order)
        positionHistoryCopy.sort((pos1, pos2) -> Long.compare(pos1.getTimestamp(), pos2.getTimestamp()));

        System.out.println("[DEBUG] Sorted Position History");

        // Now, find the positions bracketing the target time
        for (Position currentPosition : positionHistoryCopy) {
            if (currentPosition.getTimestamp() <= targetTime) {
                laterPosition = currentPosition;
            } else if (currentPosition.getTimestamp() > targetTime && earlierPosition == null) {
                earlierPosition = currentPosition;
                break; // Once found, no need to continue iterating
            }
        }

        System.out.println("[DEBUG] Later Position: " + (laterPosition != null ? laterPosition.getTimestamp() : "null"));
        System.out.println("[DEBUG] Earlier Position: " + (earlierPosition != null ? earlierPosition.getTimestamp() : "null"));

        if (earlierPosition != null && laterPosition != null) {
            long timeDiff = laterPosition.getTimestamp() - earlierPosition.getTimestamp();

            if (timeDiff == 0) {
                System.out.println("[DEBUG] Avoided division by zero, returning later position.");
                return laterPosition;
            }

            double deltaX = laterPosition.getX() - earlierPosition.getX();
            double deltaZ = laterPosition.getZ() - earlierPosition.getZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            double speed = distance / timeDiff;
            long timeToTarget = targetTime - laterPosition.getTimestamp();
            double distanceToTarget = speed * timeToTarget;
            double directionX = deltaX / distance;
            double directionZ = deltaZ / distance;

            double estimatedX = laterPosition.getX() + directionX * distanceToTarget;
            double estimatedZ = laterPosition.getZ() + directionZ * distanceToTarget;
            double estimatedY = laterPosition.getY();

            // Rounding to ensure precision
            estimatedX = Math.round(estimatedX * 1e15) / 1e15;
            estimatedZ = Math.round(estimatedZ * 1e15) / 1e15;
            estimatedY = Math.round(estimatedY * 1e15) / 1e15;

            System.out.println("[DEBUG] Estimated Position: X=" + estimatedX +
                    ", Y=" + estimatedY + ", Z=" + estimatedZ);

            return new Position(estimatedX, estimatedY, estimatedZ, targetTime);
        }

        System.out.println("[DEBUG] No valid positions found, returning null.");
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