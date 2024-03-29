/*
 * Copyright (c) 2011 Alexey Zhidkov (Jdev). All Rights Reserved.
 */

package lxx.targeting;

import lxx.BasicRobot;
import lxx.utils.*;
import robocode.*;
import robocode.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.*;

/**
 * User: jdev
 * Date: 25.07.2009
 */

public class Target implements LXXRobot, Serializable {

    private static final double INITIAL_GUN_HEAT = 3.0;

    private final List<Event> eventsList = new ArrayList<Event>();

    private final String name;
    private transient final BasicRobot owner;

    private boolean isAlive = true;

    private TargetState prevState;
    private TargetState state;
    private final TargetInfo info;

    public Target(BasicRobot owner, String name) {
        this.owner = owner;
        this.name = name;

        info = new TargetInfo();
    }

    public void addEvent(Event e) {
        eventsList.add(e);
    }

    public void update() {
        ensureValid();
        prevState = state;

        state = mergeEvents();
        state.calculateState(prevState);

        if (prevState != null) {
            info.update(prevState, state);
        }

        if (prevState != null && prevState.time + 1 != state.time && prevState.time >= 10) {
            // todo (zhidkov): notify listeners
            System.out.println("[WARN]: scans for " + getName() + " skipped: " + (state.time - prevState.time));
        }

        eventsList.clear();
    }

    private TargetState mergeEvents() {
        final TargetState newState = createState();

        for (Event event : eventsList) {
            if (event instanceof ScannedRobotEvent) {
                ScannedRobotEvent e = (ScannedRobotEvent) event;
                processScannedRobotEvent(newState, e);
            } else if (event instanceof HitRobotEvent) {
                HitRobotEvent e = (HitRobotEvent) event;
                processHitRobotEvent(newState, e);
            } else if (event instanceof BulletHitEvent) {
                BulletHitEvent e = (BulletHitEvent) event;
                processBulletHitEvent(newState, e);
            } else if (event instanceof HitByBulletEvent) {
                HitByBulletEvent e = (HitByBulletEvent) event;
                processHitByBulletEvent(newState, e);
            }
        }
        return newState;
    }

    private TargetState createState() {
        final TargetState newState = new TargetState();

        if (prevState != null) {
            newState.headingRadians = prevState.headingRadians;
            newState.velocity = prevState.velocity;
            newState.position = prevState.position;
            newState.energy = prevState.energy;
        } else {
            newState.headingRadians = 0;
            newState.velocity = 0;
            newState.position = new LXXPoint();
            newState.energy = 100;
        }

        return newState;
    }

    private void processHitByBulletEvent(TargetState newState, HitByBulletEvent e) {
        final double bulletPower = e.getBullet().getPower();
        newState.energy = prevState.energy + Rules.getBulletHitBonus(bulletPower);
        info.enemyLastHitTime = e.getTime();
        info.enemyLastCollectedEnergy = LXXUtils.getReturnedEnergy(bulletPower);
        info.enemyLastFirePower = bulletPower;
    }

    private void processBulletHitEvent(TargetState newState, BulletHitEvent e) {
        info.myLastHitTime = e.getTime();
        info.myLastDamage = Rules.getBulletDamage(e.getBullet().getPower());

        newState.update(e);
    }

    private void processHitRobotEvent(TargetState newState, HitRobotEvent e) {
        double absoluteBearing = owner.getHeadingRadians() + e.getBearingRadians();

        newState.position = (LXXPoint) owner.project(absoluteBearing, LXXConstants.ROBOT_SIDE_SIZE);
        newState.energy = e.getEnergy();
    }

    private void processScannedRobotEvent(TargetState newState, ScannedRobotEvent e) {
        final double absoluteBearing = owner.getHeadingRadians() + e.getBearingRadians();
        final APoint coords = owner.project(absoluteBearing, e.getDistance());

        newState.position = (LXXPoint) coords;
        newState.headingRadians = e.getHeadingRadians();
        newState.velocity = e.getVelocity();
        newState.energy = e.getEnergy();

        isAlive = true;
    }

    public void endRound() {
        isAlive = false;
    }

    public long getUpdateTime() {
        ensureValid();
        return state.time;
    }

    // todo(zhidkov): will be used in melee radar
    @SuppressWarnings({"UnusedDeclaration"})
    public int getLatency() {
        ensureValid();
        return (int) (owner.getTime() - state.time);
    }

    public String getName() {
        return name;
    }

    public long getTime() {
        ensureValid();
        return owner.getTime();
    }

    public double getEnergy() {
        ensureValid();
        return state.energy;
    }

    public double getVelocity() {
        ensureValid();
        return state.velocity;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setNotAlive() {
        ensureValid();
        isAlive = false;
    }

    public double getX() {
        ensureValid();
        return state.position.x;
    }

    public double getY() {
        ensureValid();
        return state.position.y;
    }

    public double aDistance(APoint p) {
        ensureValid();
        return state.position.aDistance(p);
    }

    public double angleTo(APoint target) {
        return state.position.angleTo(target);
    }

    public APoint project(double alpha, double distance) {
        return state.position.project(alpha, distance);
    }

    public APoint project(DeltaVector dv) {
        return state.position.project(dv);
    }

    public double getAbsoluteHeadingRadians() {
        ensureValid();
        return state.getAbsoluteHeadingRadians();
    }

    public long getLastStopTime() {
        ensureValid();
        return info.lastStopTime;
    }

    public long getLastTravelTime() {
        ensureValid();
        return info.lastTravelTime;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Target target = (Target) o;

        return name.equals(target.name);
    }

    public int hashCode() {
        return name.hashCode();
    }

    public double getAcceleration() {
        return state.acceleration;
    }

    private void ensureValid() {
        if (!owner.isAlive() && state != null) {
            throw new RuntimeException("Something wrong");
        }
    }

    public double getWidth() {
        ensureValid();
        return owner.getWidth();
    }

    public double getHeight() {
        ensureValid();
        return owner.getHeight();
    }

    public boolean isFireLastTick() {
        ensureValid();
        double energyDiff = getExpectedEnergy() - state.energy;
        return energyDiff > 0.09 && energyDiff < 3.1;
    }

    public double getExpectedEnergy() {
        ensureValid();
        if (prevState == null) {
            return state.energy;
        }

        double expectedEnergy = prevState.energy;
        if (owner.getTime() == info.myLastHitTime) {
            expectedEnergy -= info.myLastDamage;
        }

        if (owner.getTime() == info.enemyLastHitTime) {
            expectedEnergy += info.enemyLastCollectedEnergy;
        }

        if (isHitWall()) {
            expectedEnergy -= Rules.getWallHitDamage(prevState.velocity);
        }

        return expectedEnergy;
    }

    public boolean isHitWall() {
        ensureValid();
        if (prevState == null) {
            return false;
        }

        if (abs(prevState.velocity) - abs(state.velocity) > Rules.DECELERATION) {
            return true;
        }

        return prevState.position.aDistance(state.position) -
                prevState.position.aDistance(prevState.position.project(getAbsoluteHeadingRadians(), abs(state.velocity))) < -1.1;
    }

    public double getVelocityModule() {
        ensureValid();
        return abs(state.velocity);
    }

    // todo(zhidkov): will be used later
    @SuppressWarnings({"UnusedDeclaration"})
    public long getTurnTime() {
        ensureValid();
        return owner.getTime() - info.lastNotTurnTime;
    }

    public TargetState getPrevState() {
        return prevState;
    }

    public TargetState getState() {
        return state;
    }

    public double getFirePower() {
        return info.enemyLastFirePower;
    }

    // todo(zhidkov): implement me
    @SuppressWarnings({"UnusedDeclaration"})
    public GunType getGunType() {
        return GunType.HEAD_ON;
    }

    public double getGunHeat() {
        return state.gunHeat;
    }

    public class TargetState implements APoint, LXXRobotState {

        private final Long time;

        private LXXPoint position;
        private double headingRadians;
        private double velocity;
        private double energy;

        private double turnRateRadians;
        private double acceleration;
        private double gunHeat;

        public TargetState() {
            time = owner.getTime();
        }

        private void calculateState(TargetState prevState) {

            turnRateRadians = calculateTurnRate(prevState);
            acceleration = calculateAcceleration(prevState);
            if (prevState == null) {
                gunHeat = INITIAL_GUN_HEAT - owner.getGunCoolingRate() * owner.getTime();
            } else if (Utils.isNear(state.gunHeat, 0) && isFireLastTick()) {
                gunHeat = Rules.getGunHeat(getExpectedEnergy() - state.energy);
            } else {
                gunHeat = prevState.gunHeat;
            }

            gunHeat = max(0, gunHeat - owner.getGunCoolingRate());
        }

        private double calculateTurnRate(TargetState prevState) {
            if (prevState == null) {
                return 0;
            }

            double turnRate = Utils.normalRelativeAngle(state.headingRadians - prevState.headingRadians);

            if (abs(turnRate) > Rules.MAX_TURN_RATE_RADIANS + 0.01) {
                if (state.time == prevState.time + 1) {
                    System.out.println("[WARN] new headingRadians delta: " + toDegrees(turnRate));
                    turnRate = Rules.MAX_TURN_RATE_RADIANS * signum(turnRate);
                } else {
                    turnRate = turnRate / (time - prevState.time);
                    if (abs(turnRate) > Rules.MAX_TURN_RATE_RADIANS) {
                        turnRate = Rules.MAX_TURN_RATE_RADIANS * signum(turnRate);
                    }
                }
            }

            return turnRate;
        }

        private double calculateAcceleration(TargetState prevState) {
            if (prevState == null) {
                return 0;
            }

            double acceleration = abs(velocity) - abs(prevState.velocity);

            if (acceleration > Rules.ACCELERATION) {
                acceleration = Rules.ACCELERATION;
            }

            return acceleration;
        }

        public double getX() {
            return position.getX();
        }

        public double getY() {
            return position.getY();
        }

        public double aDistance(APoint p) {
            return position.aDistance(p);
        }

        public double angleTo(APoint pnt) {
            return position.angleTo(pnt);
        }

        public APoint project(double alpha, double distance) {
            return position.project(alpha, distance);
        }

        public APoint project(DeltaVector dv) {
            return position.project(dv);
        }

        public Long getTime() {
            return time;
        }

        public LXXPoint getPosition() {
            return position;
        }

        public double getAbsoluteHeadingRadians() {
            if (velocity >= 0) {
                return headingRadians;
            } else {
                return Utils.normalAbsoluteAngle(headingRadians + Math.PI);
            }
        }

        public double getVelocity() {
            return velocity;
        }

        public double getVelocityModule() {
            return abs(velocity);
        }

        public LXXRobot getRobot() {
            return Target.this;
        }

        public double getHeadingRadians() {
            return headingRadians;
        }

        public BattleField getBattleField() {
            return owner.battleField;
        }

        public void update(BulletHitEvent e) {
            position = new LXXPoint(e.getBullet().getX(), e.getBullet().getY());
            energy = e.getEnergy();
        }

        public double getTurnRateRadians() {
            return turnRateRadians;
        }
    }

    public class TargetInfo {

        private long lastStopTime;
        private long lastTravelTime;

        private APoint lastStopPos;

        private long myLastHitTime;
        private double myLastDamage;
        private long lastNotTurnTime;

        private long enemyLastHitTime;
        private double enemyLastCollectedEnergy;
        private double enemyLastFirePower;

        private void update(TargetState prevState, TargetState curState) {
            if (lastStopPos == null || curState.velocity == 0) {
                lastStopPos = curState;
            }

            if (Utils.isNear(curState.turnRateRadians, 0) || signum(prevState.turnRateRadians) != signum(curState.turnRateRadians)) {
                lastNotTurnTime = owner.getTime();
            }

            if (curState.velocity == 0) {
                lastStopTime = curState.time;
            } else {
                lastTravelTime = curState.time;
            }
        }
    }

    public LXXPoint getPosition() {
        return state.position;
    }
}
