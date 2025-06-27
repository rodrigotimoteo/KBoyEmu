package cpu.instructions.alu

import cpu.registers.RegisterNames

/**
 * @author rodrigotimoteo
 **/
interface ArithmeticOperations {
    fun add(register: RegisterNames)
    fun addSpecial(memoryAddress: Int, useHL: Boolean)
    fun adc(register: RegisterNames)
    fun adcSpecial(memoryAddress: Int, useHL: Boolean)
    fun sub(register: RegisterNames)
    fun subSpecial(memoryAddress: Int, useHL: Boolean)
    fun sbc(register: RegisterNames)
    fun sbcSpecial(memoryAddress: Int, useHL: Boolean)
    fun and(register: RegisterNames)
    fun andSpecial(memoryAddress: Int, useHL: Boolean)
    fun or(register: RegisterNames)
    fun orSpecial(memoryAddress: Int, useHL: Boolean)
    fun cp(register: RegisterNames)
    fun cpSpecial(memoryAddress: Int, useHL: Boolean)
}