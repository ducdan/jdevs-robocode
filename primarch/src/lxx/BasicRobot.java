/*
 * Copyright (c) 2011 Alexey Zhidkov (Jdev). All Rights Reserved.
 */

package lxx;

import lxx.events.LXXKeyEvent;
import lxx.utils.*;
import robocode.*;
import robocode.util.Utils;

import java.awt.event.KeyEvent;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.lang.Math.abs;
import static java.lang.Math.signum;

/**
 * User: jdev
 * Date: 24.10.2009
 */
public class BasicRobot extends TeamRobot implements APoint, LXXRobot {

    private final Set<RobotListener> listeners = new LinkedHashSet<RobotListener>();
    private final LXXPoint position = new LXXPoint();

    private int initialOthers;
    public BattleField battleField;

    private double prevVelocity;
    private double prevHeadingRadians;
    private long lastStopTime;
    private long lastTravelTime;
    private double acceleration;
    private int lastDirection = 1;

    protected void init() {
        initialOthers = getOthers();
        battleField = new BattleField(LXXConstants.ROBOT_SIDE_HALF_SIZE, LXXConstants.ROBOT_SIDE_HALF_SIZE,
                (int) getBattleFieldWidth() - LXXConstants.ROBOT_SIDE_SIZE, (int) getBattleFieldHeight() - LXXConstants.ROBOT_SIDE_SIZE);

        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);
        setAdjustRadarForRobotTurn(true);
    }

    public double angleTo(APoint point) {
        return LXXUtils.angle(position, point);
    }

    public APoint project(double alpha, double distance) {
        return position.project(alpha, distance);
    }

    public APoint project(DeltaVector dv) {
        return position.project(dv);
    }

    public double aDistance(APoint p) {
        return position.aDistance(p);
    }

    public void onBulletHitBullet(BulletHitBulletEvent event) {
        notifyListeners(event);
    }

    public void onHitByBullet(HitByBulletEvent event) {
        notifyListeners(event);
    }

    public void onBattleEnded(BattleEndedEvent event) {
        notifyListeners(event);
    }

    public void onScannedRobot(ScannedRobotEvent event) {
        notifyListeners(event);
    }

    public void onRobotDeath(RobotDeathEvent event) {
        notifyListeners(event);
    }

    public void onHitRobot(HitRobotEvent event) {
        notifyListeners(event);
    }

    public void onBulletHit(BulletHitEvent event) {
        notifyListeners(event);
    }

    public void onRoundEnded(RoundEndedEvent event) {
        notifyListeners(event);
    }

    public void onBulletMissed(BulletMissedEvent event) {
        notifyListeners(event);
    }

    public void onWin(WinEvent event) {
        notifyListeners(event);
    }

    public void onSkippedTurn(SkippedTurnEvent event) {
        notifyListeners(event);
    }

    public void onKeyTyped(KeyEvent e) {
        notifyListeners(new LXXKeyEvent(e.getKeyChar()));
    }

    public void addListener(RobotListener listener) {
        listeners.add(listener);
    }

    protected void notifyListeners(Event event) {
        for (RobotListener listener : listeners) {
            try {
                listener.onEvent(event);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    public void onHitWall(HitWallEvent event) {
        notifyListeners(event);
    }

    public double getAbsoluteHeadingRadians() {
        if (signum(getVelocity()) == 1) {
            return getHeadingRadians();
        } else if (signum(getVelocity()) == -1) {
            return Utils.normalAbsoluteAngle(getHeadingRadians() + Math.PI);
        } else if (lastDirection == 1) {
            return getHeadingRadians();
        } else {
            return Utils.normalAbsoluteAngle(getHeadingRadians() + Math.PI);
        }
    }

    public double getVelocityModule() {
        return abs(getVelocity());
    }

    public boolean isAlive() {
        return true;
    }

    public LXXRobotState getState() {
        return new RobotSnapshot(this);
    }

    public int getInitialOthers() {
        return initialOthers;
    }

    public boolean isDuel() {
        return initialOthers == 1;
    }

    public LXXGraphics getLXXGraphics() {
        return new LXXGraphics(getGraphics());
    }

    public void onStatus(StatusEvent e) {
        acceleration = abs(getVelocity()) - abs(prevVelocity);
        if (acceleration < -Rules.DECELERATION - 0.01) {
            System.out.println("[WARN] acceleration: " + acceleration);
            acceleration = -Rules.DECELERATION;
        } else if (acceleration > Rules.ACCELERATION + 0.01) {
            System.out.println("[WARN] acceleration: " + acceleration);
            acceleration = Rules.ACCELERATION;
        }
        prevVelocity = getVelocity();
        if (Utils.isNear(getVelocity(), 0)) {
            lastStopTime = e.getTime();
        } else {
            lastTravelTime = e.getTime();
        }
        position.x = e.getStatus().getX();
        position.y = e.getStatus().getY();

        if (abs(e.getStatus().getVelocity()) >= 0.1) {
            lastDirection = (int) signum(e.getStatus().getVelocity());
        }

        prevHeadingRadians = getHeadingRadians();

        super.onStatus(e);

        notifyListeners(e);
    }

    public double getX() {
        return position.x;
    }

    public double getY() {
        return position.y;
    }

    public long getLastStopTime() {
        return lastStopTime;
    }

    public long getLastTravelTime() {
        return lastTravelTime;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public LXXPoint getPosition() {
        return position;
    }

    public int hashCode() {
        return getName().hashCode();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicRobot basicRobot = (BasicRobot) o;

        return getName().equals(basicRobot.getName());
    }

    public double getTurnRateRadians() {
        return prevHeadingRadians - getHeadingRadians();
    }
}