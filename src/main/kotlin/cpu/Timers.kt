package cpu

import memory.Bus

/**
 * Class purposed with handling everything that needs timings inside the CPU
 * total Cycles, interrupts and others
 *
 * @author rodrigotimoteo
 **/
class Timers {

    /**
     * Stores whether the timer is currently enabled (true if enabled false otherwise)
     */
    private var timerEnabled: Boolean = true

    /**
     * Stores if there has been a timer overflow
     */
    private var handleOverflow: Boolean = false

    /**
     * Stores the amount of executed machine cycles
     */
    private var machineCycles: Int = 0

    /**
     * Stores the cycles when halt was last triggered
     */
    private var haltCycleCounter: Int = 0

    /**
     * Stores the cycles when interrupt status was last changed
     */
    private var interruptChangedCounter: Int = 0

    /**
     * Advances the timers by one unit
     */
    public fun tick() {
        machineCycles++

        tickDividerTimer()
        tickNormalTimer()
    }

    private fun tickDividerTimer() {}

    private fun tickNormalTimer() {}

    /**
     * Machine cycles getter method
     *
     * @return machine cycle counter
     */
    public fun getMachineCycles(): Int = machineCycles

    /**
     * Halt cycles getter method
     *
     * @return halt cycle counter
     */
    public fun getHaltCycleCounter(): Int = haltCycleCounter

    /**
     * Halt cycles counter for when Halt is triggered
     */
    public fun setHaltCycleCounter() {
        haltCycleCounter = machineCycles
    }

    /**
     * Getter for the last time interrupt change was triggered in machine cycles
     *
     * @return machine cycle count
     */
    public fun getInterruptChangedCounter(): Int = interruptChangedCounter

    /**
     * Setter for the last time interrupt status was changed
     */
    public fun setInterruptChangedCounter() {
        interruptChangedCounter = machineCycles
    }
}
