package cpu.instructions

import cpu.registers.RegisterNames
import memory.Bus
import memory.BusConstants
import kotlin.system.exitProcess

/**
 * @author rodrigotimoteo
 **/
@Suppress("MagicNumber", "LargeClass")
class Decoder(
    private val bus: Bus
) {
    /**
     * Holds reference for Alu instruction handler
     */
    private val alu: Alu = Alu(bus)

    /**
     * Holds reference for Cpu Control instruction handler
     */
    private val control: Control = Control(bus)

    /**
     * Holds reference for Jump instruction handler
     */
    private val jump: Jump = Jump(bus)

    /**
     * Holds reference for 8 Bit Loads instruction handler
     */
    private val load8Bit: Load8Bit = Load8Bit(bus)

    /**
     * Holds reference for 16 Bit Loads instruction handler
     */
    private val load16Bit: Load16Bit = Load16Bit(bus)

    /**
     * Holds reference for Rotates and Shifts instruction handler
     */
    private val rotateShift: RotateShift = RotateShift(bus)

    /**
     * Holds reference for Single Bit instruction handler
     */
    private val singleBit: SingleBit = SingleBit(bus)

    /**
     * Stores whether the next instruction is of the secondary opcode table
     * prefixed by 0xCB
     */
    private var cbInstruction: Boolean = false

    /**
     * Decodes operation code to be executed and handles the operation to the helper classes
     *
     * @param operationCode to be executed
     */
    public fun decode(operationCode: Int) {
        bus.executeFromCPU(BusConstants.TICK_TIMERS, Bus.EMPTY_ARGUMENTS)

        if (!cbInstruction) {
            handleRegularOPs(operationCode)
        } else {
            handleCBOPs(operationCode)
            cbInstruction = false
        }
    }

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    private fun handleRegularOPs(operationCode: Int) {
        when (operationCode) {
            0x00 ->  // NOP
                control.nop()
            0x01 ->  // LD BC,u16
                load16Bit.ld16bit(0)
            0x02 ->  // LD (BC),A
                load8Bit.ldTwoRegisters(0)
            0x03 ->  // INC BC
                alu.incR(0)
            0x04 ->  // INC B
                alu.inc(RegisterNames.B)
            0x05 ->  // DEC B
                alu.dec(RegisterNames.B)
            0x06 ->  // LD B,u8
                load8Bit.ldNRegister(RegisterNames.B)
            0x07 ->  // RLCA
                rotateShift.rlca()
            0x08 ->  // LD (u16),SP
                load16Bit.ldNNSP()
            0x09 ->  // ADD HL,BC
                alu.addHL(0)
            0x0A ->  // LD A,(BC)
                load8Bit.ldTwoRegistersIntoA(0)
            0x0B ->  // DEC BC
                alu.decR(0)
            0x0C ->  // INC C
                alu.inc(RegisterNames.C)
            0x0D ->  // DEC C
                alu.dec(RegisterNames.C)
            0x0E ->  // LD C,u8
                load8Bit.ldNRegister(RegisterNames.C)
            0x0F ->  // RRCA
                rotateShift.rrca()
            0x10 ->  // STOP
                control.stop()
            0x11 ->  // LD DE,u16
                load16Bit.ld16bit(1)
            0x12 ->  // LD (DE),A
                load8Bit.ldTwoRegisters(1)
            0x13 ->  // INC DE
                alu.incR(1)
            0x14 ->  // INC D
                alu.inc(RegisterNames.D)
            0x15 ->  // DEC D
                alu.dec(RegisterNames.D)
            0x16 ->  // LD D,u8
                load8Bit.ldNRegister(RegisterNames.D)
            0x17 ->  // RLA
                rotateShift.rla()
            0x18 ->  // JR i8
                jump.jr()
            0x19 ->  // ADD HL,DE
                alu.addHL(1)
            0x1A ->  // LD A,(DE)
                load8Bit.ldTwoRegistersIntoA(1)
            0x1B ->  // DEC DE
                alu.decR(1)
            0x1C ->  // INC E
                alu.inc(RegisterNames.E)
            0x1D ->  // DEC E
                alu.dec(RegisterNames.E)
            0x1E ->  // LD E,u8
                load8Bit.ldNRegister(RegisterNames.E)
            0x1F ->  // RRA
                rotateShift.rra()
            0x20 ->  // JR NZ,i8
                jump.jrCond("NZ")
            0x21 ->  // LD HL,u16
                load16Bit.ld16bit(2)
            0x22 ->  // LDI (HL),A
                load8Bit.ldi(true)
            0x23 ->  // INC HL
                alu.incR(2)
            0x24 ->  // INC H
                alu.inc(RegisterNames.H)
            0x25 ->  // DEC H
                alu.dec(RegisterNames.H)
            0x26 ->  // LD H,u8
                load8Bit.ldNRegister(RegisterNames.H)
            0x27 ->  // DAA
                alu.daa()
            0x28 ->  // JR Z,u8
                jump.jrCond("Z")
            0x29 ->  // ADD HL, HL
                alu.addHL(2)
            0x2A ->  // LDI A,(HL)
                load8Bit.ldi(false)
            0x2B ->  // DEC HL
                alu.decR(2)
            0x2C ->  // INC L
                alu.inc(RegisterNames.L)
            0x2D ->  // DEC L
                alu.dec(RegisterNames.L)
            0x2E ->  // LD L,u8
                load8Bit.ldNRegister(RegisterNames.L)
            0x2F ->  // CPL
                alu.cpl()
            0x30 ->  // JR NC,u8
                jump.jrCond("NC")
            0x31 ->  // LD SP,u16
                load16Bit.ldSPUU()
            0x32 ->  // LDD (HL),A
                load8Bit.ldd(true)
            0x33 ->  // INC SP
                alu.incSP()
            0x34 ->  // INC (HL)
                alu.incSpecial(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x35 ->  // INC (HL)
                alu.decSpecial(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x36 ->  // LD (HL), n
                load8Bit.ldNHL()
            0x37 ->  // SCF
                control.scf()
            0x38 ->  // JR C,u8
                jump.jrCond(RegisterNames.C)
            0x39 ->  // ADD HL,SP
                alu.addHLSP()
            0x3A ->  // LDD A,(HL)
                load8Bit.ldd(false)
            0x3B ->  // DEC SP
                alu.decSP()
            0x3C ->  // INC A
                alu.inc(RegisterNames.A)
            0x3D ->  // DEC A
                alu.dec(RegisterNames.A)
            0x3E ->  // LD A,u8
                load8Bit.ldNRegister(RegisterNames.A)
            0x3F ->  // CCF
                control.ccf()
            0x40 ->  // LD B,B
                load8Bit.ld(RegisterNames.B, RegisterNames.B)
            0x41 ->  // LD B,C
                load8Bit.ld(RegisterNames.B, RegisterNames.C)
            0x42 ->  // LD B,D
                load8Bit.ld(RegisterNames.B, RegisterNames.D)
            0x43 ->  // LD B,E
                load8Bit.ld(RegisterNames.B, RegisterNames.E)
            0x44 ->  // LD B,H
                load8Bit.ld(RegisterNames.B, RegisterNames.H)
            0x45 ->  // LD B,L
                load8Bit.ld(RegisterNames.B, RegisterNames.L)
            0x46 ->  // LD B,(HL)
                load8Bit.ldHLtoRegister(RegisterNames.B)
            0x47 ->  // LD B,A
                load8Bit.ld(RegisterNames.B, RegisterNames.A)
            0x48 ->  // LD C,B
                load8Bit.ld(RegisterNames.C, RegisterNames.B)
            0x49 ->  // LD C,C
                load8Bit.ld(RegisterNames.C, RegisterNames.C)
            0x4A ->  // LD C,D
                load8Bit.ld(RegisterNames.C, RegisterNames.D)
            0x4B ->  // LD C,E
                load8Bit.ld(RegisterNames.C, RegisterNames.E)
            0x4C ->  // LD C,H
                load8Bit.ld(RegisterNames.C, RegisterNames.H)
            0x4D ->  // LD C,L
                load8Bit.ld(RegisterNames.C, RegisterNames.L)
            0x4E ->  // LD C,(HL)
                load8Bit.ldHLtoRegister(RegisterNames.C)
            0x4F ->  // LD C,A
                load8Bit.ld(RegisterNames.C, RegisterNames.A)
            0x50 ->  // LD D,B
                load8Bit.ld(RegisterNames.D, RegisterNames.B)
            0x51 ->  // LD D,C
                load8Bit.ld(RegisterNames.D, RegisterNames.C)
            0x52 ->  // LD D,D
                load8Bit.ld(RegisterNames.D, RegisterNames.D)
            0x53 ->  // LD D,E
                load8Bit.ld(RegisterNames.D, RegisterNames.E)
            0x54 ->  // LD D,H
                load8Bit.ld(RegisterNames.D, RegisterNames.H)
            0x55 ->  // LD D,L
                load8Bit.ld(RegisterNames.D, RegisterNames.L)
            0x56 ->  // LD D,(HL)
                load8Bit.ldHLtoRegister(RegisterNames.D)
            0x57 ->  // LD D,A
                load8Bit.ld(RegisterNames.D, RegisterNames.A)
            0x58 ->  // LD E,B
                load8Bit.ld(RegisterNames.E, RegisterNames.B)
            0x59 ->  // LD E,C
                load8Bit.ld(RegisterNames.E, RegisterNames.C)
            0x5A ->  // LD E,D
                load8Bit.ld(RegisterNames.E, RegisterNames.D)
            0x5B ->  // LD E,E
                load8Bit.ld(RegisterNames.E, RegisterNames.E)
            0x5C ->  // LD E,H
                load8Bit.ld(RegisterNames.E, RegisterNames.H)
            0x5D ->  // LD E,L
                load8Bit.ld(RegisterNames.E, RegisterNames.L)
            0x5E ->  // LD E,(HL)
                load8Bit.ldHLtoRegister(RegisterNames.E)
            0x5F ->  // LD E,A
                load8Bit.ld(RegisterNames.E, RegisterNames.A)
            0x60 ->  // LD H,B
                load8Bit.ld(RegisterNames.H, RegisterNames.B)
            0x61 ->  // LD H,C
                load8Bit.ld(RegisterNames.H, RegisterNames.C)
            0x62 ->  // LD H,D
                load8Bit.ld(RegisterNames.H, RegisterNames.D)
            0x63 ->  // LD H,E
                load8Bit.ld(RegisterNames.H, RegisterNames.E)
            0x64 ->  // LD H,H
                load8Bit.ld(RegisterNames.H, RegisterNames.H)
            0x65 ->  // LD H,L
                load8Bit.ld(RegisterNames.H, RegisterNames.L)
            0x66 ->  // LD H,(HL)
                load8Bit.ldHLtoRegister(RegisterNames.H)
            0x67 ->  // LD H,A
                load8Bit.ld(RegisterNames.H, RegisterNames.A)
            0x68 ->  // LD L,B
                load8Bit.ld(RegisterNames.L, RegisterNames.B)
            0x69 ->  // LD L,C
                load8Bit.ld(RegisterNames.L, RegisterNames.C)
            0x6A ->  // LD L,D
                load8Bit.ld(RegisterNames.L, RegisterNames.D)
            0x6B ->  // LD L,E
                load8Bit.ld(RegisterNames.L, RegisterNames.E)
            0x6C ->  // LD L,H
                load8Bit.ld(RegisterNames.L, RegisterNames.H)
            0x6D ->  // LD L,L
                load8Bit.ld(RegisterNames.L, RegisterNames.L)
            0x6E ->  // LD L,(HL)
                load8Bit.ldHLtoRegister(RegisterNames.L)
            0x6F ->  // LD L,A
                load8Bit.ld(RegisterNames.L, RegisterNames.A)
            0x70 ->  // LD (HL),B
                load8Bit.ldRtoHL(RegisterNames.B)
            0x71 ->  // LD (HL),C
                load8Bit.ldRtoHL(RegisterNames.C)
            0x72 ->  // LD (HL),D
                load8Bit.ldRtoHL(RegisterNames.D)
            0x73 ->  // LD (HL),E
                load8Bit.ldRtoHL(RegisterNames.E)
            0x74 ->  // LD (HL),H
                load8Bit.ldRtoHL(RegisterNames.H)
            0x75 ->  // LD (HL),L
                load8Bit.ldRtoHL(RegisterNames.L)
            0x76 ->  // HALT
                control.halt()
            0x77 ->  // LD (HL),A
                load8Bit.ldTwoRegisters(2)
            0x78 ->  // LD A,B
                load8Bit.ld(RegisterNames.A, RegisterNames.B)
            0x79 ->  // LD A,C
                load8Bit.ld(RegisterNames.A, RegisterNames.C)
            0x7A ->  // LD A,D
                load8Bit.ld(RegisterNames.A, RegisterNames.D)
            0x7B ->  // LD A,E
                load8Bit.ld(RegisterNames.A, RegisterNames.E)
            0x7C ->  // LD A,H
                load8Bit.ld(RegisterNames.A, RegisterNames.H)
            0x7D ->  // LD A,L
                load8Bit.ld(RegisterNames.A, RegisterNames.L)
            0x7E ->  // LD A,(HL)
                load8Bit.ldTwoRegistersIntoA(2)
            0x7F ->  // LD A,A
                load8Bit.ld(RegisterNames.A, RegisterNames.A)
            0x80 ->  // ADD A,B
                alu.add(RegisterNames.B)
            0x81 ->  // ADD A,C
                alu.add(RegisterNames.C)
            0x82 ->  // ADD A,D
                alu.add(RegisterNames.D)
            0x83 ->  // ADD A,E
                alu.add(RegisterNames.E)
            0x84 ->  // ADD A, H
                alu.add(RegisterNames.H)
            0x85 ->  // ADD A,L
                alu.add(RegisterNames.L)
            0x86 ->  // ADD A,(HL)
                alu.addSpecial((bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int), true)
            0x87 ->  // ADD A,A
                alu.add(RegisterNames.A)
            0x88 ->  // ADC A,B
                alu.adc(RegisterNames.B)
            0x89 ->  // ADC A,C
                alu.adc(RegisterNames.C)
            0x8A ->  // ADC A,D
                alu.adc(RegisterNames.D)
            0x8B ->  // ADC A,E
                alu.adc(RegisterNames.E)
            0x8C ->  // ADC A,H
                alu.adc(RegisterNames.H)
            0x8D ->  // ADC A,L
                alu.adc(RegisterNames.L)
            0x8E ->  // ADC A,(HL)
                alu.adcSpecial(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int, true)
            0x8F ->  // ADC A,A
                alu.adc(RegisterNames.A)
            0x90 ->  // SUB A,B
                alu.sub(RegisterNames.B)
            0x91 ->  // SUB A,C
                alu.sub(RegisterNames.C)
            0x92 ->  // SUB A,D
                alu.sub(RegisterNames.D)
            0x93 ->  // SUB A,E
                alu.sub(RegisterNames.E)
            0x94 ->  // SUB A,H
                alu.sub(RegisterNames.H)
            0x95 ->  // SUB A,L
                alu.sub(RegisterNames.L)
            0x96 ->  // SUB A, (HL)
                alu.subSpecial(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int, true)
            0x97 ->  // SUB A,A
                alu.sub(RegisterNames.A)
            0x98 ->  // SBC A,B
                alu.sbc(RegisterNames.B)
            0x99 ->  // SBC A,C
                alu.sbc(RegisterNames.C)
            0x9A ->  // SBC A,D
                alu.sbc(RegisterNames.D)
            0x9B ->  // SBC A,E
                alu.sbc(RegisterNames.E)
            0x9C ->  // SBC A,H
                alu.sbc(RegisterNames.H)
            0x9D ->  // SBC A,L
                alu.sbc(RegisterNames.L)
            0x9E ->  // SBC A, (HL)
                alu.sbcSpecial(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int, true)
            0x9F ->  // SBC A,A
                alu.sbc(RegisterNames.A)
            0xA0 ->  // AND A,B
                alu.and(RegisterNames.B)
            0xA1 ->  // AND A,C
                alu.and(RegisterNames.C)
            0xA2 ->  // AND A,D
                alu.and(RegisterNames.D)
            0xA3 ->  // AND A,E
                alu.and(RegisterNames.E)
            0xA4 ->  // AND A,H
                alu.and(RegisterNames.H)
            0xA5 ->  // AND A,L
                alu.and(RegisterNames.L)
            0xA6 ->  // AND A,(HL)
                alu.andSpecial(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int, true)
            0xA7 ->  // AND A,A
                alu.and(RegisterNames.A)
            0xA8 ->  // XOR A,B
                alu.xor(RegisterNames.B)
            0xA9 ->  // XOR A,C
                alu.xor(RegisterNames.C)
            0xAA ->  // XOR A,D
                alu.xor(RegisterNames.D)
            0xAB ->  // XOR A,E
                alu.xor(RegisterNames.E)
            0xAC ->  // XOR A,H
                alu.xor(RegisterNames.H)
            0xAD ->  // XOR A,L
                alu.xor(RegisterNames.L)
            0xAE ->  // XOR A,(HL)
                alu.xorSpecial(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int, true)
            0xAF ->  // XOR A,A
                alu.xor(RegisterNames.A)
            0xB0 ->  // OR A,B
                alu.or(RegisterNames.B)
            0xB1 ->  // OR A,C
                alu.or(RegisterNames.C)
            0xB2 ->  // OR A,D
                alu.or(RegisterNames.D)
            0xB3 ->  // OR A,E
                alu.or(RegisterNames.E)
            0xB4 ->  // OR A,H
                alu.or(RegisterNames.H)
            0xB5 ->  // OR A,L
                alu.or(RegisterNames.L)
            0xB6 ->  // OR A,(HL)
                alu.orSpecial(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int, true)
            0xB7 ->  // OR A,A
                alu.or(RegisterNames.A)
            0xB8 ->  // CP A,B
                alu.cp(RegisterNames.B)
            0xB9 ->  // CP A,C
                alu.cp(RegisterNames.C)
            0xBA ->  // CP A,D
                alu.cp(RegisterNames.D)
            0xBB ->  // CP A,E
                alu.cp(RegisterNames.E)
            0xBC ->  // CP A,H
                alu.cp(RegisterNames.H)
            0xBD ->  // CP A,L
                alu.cp(RegisterNames.L)
            0xBE ->  // CP A,(HL)
                alu.cpSpecial(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int, true)
            0xBF ->  // CP A,A
                alu.cp(RegisterNames.A)
            0xC0 ->  // RET NZ
                jump.retCond(JumpConstants.NZ)
            0xC1 ->  // POP BC
                load16Bit.pop(1)
            0xC2 ->  // JP NZ,u16
                jump.jpCond(JumpConstants.NZ)
            0xC3 ->  // JP u16
                jump.jp()
            0xC4 ->  // CALL NZ, nn
                jump.callCond(JumpConstants.NZ)
            0xC5 ->  // PUSH BC
                load16Bit.push(1)
            0xC6 ->  // ADD A,#
                alu.addSpecial(bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int + 1,
                    false)
            0xC7 ->  // RST 00H
                jump.rst(0x00)
            0xC8 ->  // RET Z
                jump.retCond(JumpConstants.Z)
            0xC9 ->  // RET
                jump.ret()
            0xCA ->  // JP Z,u16
                jump.jpCond(JumpConstants.Z)
            0xCB -> {
                cbInstruction = true
                bus.executeFromCPU(BusConstants.INCR_PC, arrayOf<String>("1"))
                decode(bus.getValue((bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int)))
            }
            0xCC ->  // CALL Z,nn
                jump.callCond(JumpConstants.Z)
            0xCD ->  // CALL u16
                jump.call()
            0xCE ->  // ADC A,#
                alu.adcSpecial(bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int + 1, false)
            0xCF ->  // RST 08H
                jump.rst(0x08)
            0xD0 ->  // RET NC
                jump.retCond(JumpConstants.NC)
            0xD1 ->  // POP DE
                load16Bit.pop(2)
            0xD2 ->  // JP NC,u16
                jump.jpCond(JumpConstants.NC)
            0xD4 ->  // CALL NC,nn
                jump.callCond(JumpConstants.NC)
            0xD5 ->  // PUSH DE
                load16Bit.push(2)
            0xD6 ->  // SUB A, #
                alu.subSpecial(bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int + 1, false)
            0xD7 ->  // RST 10H
                jump.rst(0x10)
            0xD8 ->  // RET C
                jump.retCond(JumpConstants.C)
            0xD9 ->  // RETI
                jump.reti()
            0xDA ->  // JP C,u16
                jump.jpCond(JumpConstants.C)
            0xDC ->  // CALL C,nn
                jump.callCond(JumpConstants.C)
            0xDE ->  // SBC A,#
                alu.sbcSpecial(bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int + 1, false)
            0xDF ->  // RST 18H
                jump.rst(0x18)
            0xE0 ->  // LD (FF00+u8),A
                load8Bit.ldh(true)
            0xE1 ->  // POP (HL)
                load16Bit.pop(3)
            0xE2 ->  // LD (C), A
                load8Bit.ldAC(true)
            0xE5 ->  // PUSH HL
                load16Bit.push(3)
            0xE6 ->  // AND #
                alu.andSpecial(bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int + 1, false)
            0xE7 ->  // RST 20H
                jump.rst(0x20)
            0xE8 ->  // ADD SP,n
                alu.addSP(bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int + 1)
            0xE9 ->  // JP (HL)
                jump.jpHL()
            0xEA ->  // LD (nn),A
                load8Bit.ldNN()
            0xEE ->  // XOR #
                alu.xorSpecial(bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int + 1, false)
            0xEF ->  // RST 28H
                jump.rst(0x28)
            0xF0 ->  // LD A,(FF00+u8)
                load8Bit.ldh(false)
            0xF1 ->  // POP AF
                load16Bit.pop(0)
            0xF2 ->  // LD A,(C)
                load8Bit.ldAC(false)
            0xF3 ->  // DI
                control.di()
            0xF5 ->  // PUSH AF
                load16Bit.push(0)
            0xF6 ->  // OR #
                alu.orSpecial(bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int + 1, false)
            0xF7 ->  // RST 30H
                jump.rst(0x30)
            0xF8 ->  // LDHL SP,n
                load16Bit.ldHL()
            0xF9 ->  // LD SP,HL
                load16Bit.ldSPHL()
            0xFA ->  // LD A,(nn)
                load8Bit.ldNNIntoA()
            0xFB ->  // EI
                control.ei()
            0xFE ->  // CP A,u8
                alu.cpSpecial(bus.getFromCPU(BusConstants.GET_PC, Bus.EMPTY_ARGUMENTS) as Int + 1, false)
            0xFF ->  // RST 38H
                jump.rst(0x38)
            else -> {
                println("No OPCode or Lacks Implementation")
                exitProcess(0)
            }
        }
    }

    @Suppress("CyclomaticComplexMethod", "LongMethod")
    private fun handleCBOPs(operationCode: Int) {
        when (operationCode) {
            0x00 ->  // RLC B
                rotateShift.rlc(RegisterNames.B)
            0x01 ->  // RLC C
                rotateShift.rlc(RegisterNames.C)
            0x02 ->  // RLC D
                rotateShift.rlc(RegisterNames.D)
            0x03 ->  // RLC E
                rotateShift.rlc(RegisterNames.E)
            0x04 ->  // RLC H
                rotateShift.rlc(RegisterNames.H)
            0x05 ->  // RLC L
                rotateShift.rlc(RegisterNames.L)
            0x06 ->  // RLC HL
                rotateShift.rlcHL(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x07 ->  // RLC A
                rotateShift.rlc(RegisterNames.A)
            0x08 ->  // RRC B
                rotateShift.rrc(RegisterNames.B)
            0x09 ->  // RRC C
                rotateShift.rrc(RegisterNames.C)
            0x0A ->  // RRC D
                rotateShift.rrc(RegisterNames.D)
            0x0B ->  // RRC E
                rotateShift.rrc(RegisterNames.E)
            0x0C ->  // RRC H
                rotateShift.rrc(RegisterNames.H)
            0x0D ->  // RRC L
                rotateShift.rrc(RegisterNames.L)
            0x0E ->  // RRC (HL)
                rotateShift.rrcHL(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x0F ->  // RRC A
                rotateShift.rrc(RegisterNames.A)
            0x10 ->  // RL B
                rotateShift.rl(RegisterNames.B)
            0x11 ->  // RL C
                rotateShift.rl(RegisterNames.C)
            0x12 ->  // RL D
                rotateShift.rl(RegisterNames.D)
            0x13 ->  // RL E
                rotateShift.rl(RegisterNames.E)
            0x14 ->  // RL H
                rotateShift.rl(RegisterNames.H)
            0x15 ->  // RL L
                rotateShift.rl(RegisterNames.L)
            0x16 ->  // RL (HL)
                rotateShift.rlHL(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x17 ->  // RL A
                rotateShift.rl(RegisterNames.A)
            0x18 ->  // RR B
                rotateShift.rr(RegisterNames.B)
            0x19 ->  // RR C
                rotateShift.rr(RegisterNames.C)
            0x1A ->  // RR D
                rotateShift.rr(RegisterNames.D)
            0x1B ->  // RR E
                rotateShift.rr(RegisterNames.E)
            0x1C ->  // RR H
                rotateShift.rr(RegisterNames.H)
            0x1D ->  // RR L
                rotateShift.rr(RegisterNames.L)
            0x1E ->  // RR (HL)
                rotateShift.rrHL(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x1F ->  // RR A
                rotateShift.rr(RegisterNames.A)
            0x20 ->  // SLA B
                rotateShift.sla(RegisterNames.B)
            0x21 ->  // SLA C
                rotateShift.sla(RegisterNames.C)
            0x22 ->  // SLA D
                rotateShift.sla(RegisterNames.D)
            0x23 ->  // SLA E
                rotateShift.sla(RegisterNames.E)
            0x24 ->  // SLA H
                rotateShift.sla(RegisterNames.H)
            0x25 ->  // SLA L
                rotateShift.sla(RegisterNames.L)
            0x26 ->  // SLA (HL)
                rotateShift.slaHL(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x27 ->  // SLA A
                rotateShift.sla(RegisterNames.A)
            0x28 ->  // SRA B
                rotateShift.sra(RegisterNames.B)
            0x29 ->  // SRA C
                rotateShift.sra(RegisterNames.C)
            0x2A ->  // SRA D
                rotateShift.sra(RegisterNames.D)
            0x2B ->  // SRA E
                rotateShift.sra(RegisterNames.E)
            0x2C ->  // SRA H
                rotateShift.sra(RegisterNames.H)
            0x2D ->  // SRA L
                rotateShift.sra(RegisterNames.L)
            0x2E ->  // SRA (HL)
                rotateShift.sraHL(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x2F ->  // SRA A
                rotateShift.sra(RegisterNames.A)
            0x30 ->  // SWAP B
                rotateShift.swap(RegisterNames.B)
            0x31 ->  // SWAP C
                rotateShift.swap(RegisterNames.C)
            0x32 ->  // SWAP D
                rotateShift.swap(RegisterNames.D)
            0x33 ->  // SWAP E
                rotateShift.swap(RegisterNames.E)
            0x34 ->  // SWAP H
                rotateShift.swap(RegisterNames.H)
            0x35 ->  // SWAP L
                rotateShift.swap(RegisterNames.L)
            0x36 ->  // SWAP (HL)
                rotateShift.swapHL(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x37 ->  // SWAP A
                rotateShift.swap(RegisterNames.A)
            0x38 ->  // SRL B
                rotateShift.srl(RegisterNames.B)
            0x39 ->  // SRL C
                rotateShift.srl(RegisterNames.C)
            0x3A ->  // SRL D
                rotateShift.srl(RegisterNames.D)
            0x3B ->  // SRL E
                rotateShift.srl(RegisterNames.E)
            0x3C ->  // SRL H
                rotateShift.srl(RegisterNames.H)
            0x3D ->  // SRL L
                rotateShift.srl(RegisterNames.L)
            0x3E ->  // SRL (HL)
                rotateShift.srlHL(bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x3F ->  // SRL A
                rotateShift.srl(RegisterNames.A)
            0x40 ->  // BIT 0,B
                singleBit.bit(0, RegisterNames.B)
            0x41 ->  // BIT 0,C
                singleBit.bit(0, RegisterNames.C)
            0x42 ->  // BIT 0,D
                singleBit.bit(0, RegisterNames.D)
            0x43 ->  // BIT 0,E
                singleBit.bit(0, RegisterNames.E)
            0x44 ->  // BIT 0,H
                singleBit.bit(0, RegisterNames.H)
            0x45 ->  // BIT 0,L
                singleBit.bit(0, RegisterNames.L)
            0x46 ->  // BIT 0,(HL)
                singleBit.bitHL(0, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x47 ->  // BIT 0,A
                singleBit.bit(0, RegisterNames.A)
            0x48 ->  // BIT 1,B
                singleBit.bit(1, RegisterNames.B)
            0x49 ->  // BIT 1,C
                singleBit.bit(1, RegisterNames.C)
            0x4A ->  // BIT 1,D
                singleBit.bit(1, RegisterNames.D)
            0x4B ->  // BIT 1,E
                singleBit.bit(1, RegisterNames.E)
            0x4C ->  // BIT 1,H
                singleBit.bit(1, RegisterNames.H)
            0x4D ->  // BIT 1,L
                singleBit.bit(1, RegisterNames.L)
            0x4E ->  // BIT 1,(HL)
                singleBit.bitHL(1, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x4F ->  // BIT 1,A
                singleBit.bit(1, RegisterNames.A)
            0x50 ->  // BIT 2,B
                singleBit.bit(2, RegisterNames.B)
            0x51 ->  // BIT 2,C
                singleBit.bit(2, RegisterNames.C)
            0x52 ->  // BIT 2,D
                singleBit.bit(2, RegisterNames.D)
            0x53 ->  // BIT 2,E
                singleBit.bit(2, RegisterNames.E)
            0x54 ->  // BIT 2,H
                singleBit.bit(2, RegisterNames.H)
            0x55 ->  // BIT 2,L
                singleBit.bit(2, RegisterNames.L)
            0x56 ->  // BIT 2,(HL)
                singleBit.bitHL(2, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x57 ->  // BIT 2,A
                singleBit.bit(2, RegisterNames.A)
            0x58 ->  // BIT 3,B
                singleBit.bit(3, RegisterNames.B)
            0x59 ->  // BIT 3,C
                singleBit.bit(3, RegisterNames.C)
            0x5A ->  // BIT 3,D
                singleBit.bit(3, RegisterNames.D)
            0x5B ->  // BIT 3,E
                singleBit.bit(3, RegisterNames.E)
            0x5C ->  // BIT 3,H
                singleBit.bit(3, RegisterNames.H)
            0x5D ->  // BIT 3,L
                singleBit.bit(3, RegisterNames.L)
            0x5E ->  // BIT 3,(HL)
                singleBit.bitHL(3, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x5F ->  // BIT 3,A
                singleBit.bit(3, RegisterNames.A)
            0x60 ->  // BIT 4,B
                singleBit.bit(4, RegisterNames.B)
            0x61 ->  // BIT 4,C
                singleBit.bit(4, RegisterNames.C)
            0x62 ->  // BIT 4,D
                singleBit.bit(4, RegisterNames.D)
            0x63 ->  // BIT 4,E
                singleBit.bit(4, RegisterNames.E)
            0x64 ->  // BIT 4,H
                singleBit.bit(4, RegisterNames.H)
            0x65 ->  // BIT 4,L
                singleBit.bit(4, RegisterNames.L)
            0x66 ->  // BIT 4,(HL)
                singleBit.bitHL(4, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x67 ->  // BIT 4,A
                singleBit.bit(4, RegisterNames.A)
            0x68 ->  // BIT 5,B
                singleBit.bit(5, RegisterNames.B)
            0x69 ->  // BIT 5,C
                singleBit.bit(5, RegisterNames.C)
            0x6A ->  // BIT 5,D
                singleBit.bit(5, RegisterNames.D)
            0x6B ->  // BIT 5,E
                singleBit.bit(5, RegisterNames.E)
            0x6C ->  // BIT 5,H
                singleBit.bit(5, RegisterNames.H)
            0x6D ->  // BIT 5,L
                singleBit.bit(5, RegisterNames.L)
            0x6E ->  // BIT 5,(HL)
                singleBit.bitHL(5, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x6F ->  // BIT 5,A
                singleBit.bit(5, RegisterNames.A)
            0x70 ->  // BIT 6,B
                singleBit.bit(6, RegisterNames.B)
            0x71 ->  // BIT 6,C
                singleBit.bit(6, RegisterNames.C)
            0x72 ->  // BIT 6,D
                singleBit.bit(6, RegisterNames.D)
            0x73 ->  // BIT 6,E
                singleBit.bit(6, RegisterNames.E)
            0x74 ->  // BIT 6,H
                singleBit.bit(6, RegisterNames.H)
            0x75 ->  // BIT 6,L
                singleBit.bit(6, RegisterNames.L)
            0x76 ->  // BIT 6,(HL)
                singleBit.bitHL(6, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x77 ->  // BIT 6,A
                singleBit.bit(6, RegisterNames.A)
            0x78 ->  // BIT 7,B
                singleBit.bit(7, RegisterNames.B)
            0x79 ->  // BIT 7,C
                singleBit.bit(7, RegisterNames.C)
            0x7A ->  // BIT 7,D
                singleBit.bit(7, RegisterNames.D)
            0x7B ->  // BIT 7,E
                singleBit.bit(7, RegisterNames.E)
            0x7C ->  // BIT 7,H
                singleBit.bit(7, RegisterNames.H)
            0x7D ->  // BIT 7,L
                singleBit.bit(7, RegisterNames.L)
            0x7E ->  // BIT 7, (HL)
                singleBit.bitHL(7, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x7F ->  // BIT 7,A
                singleBit.bit(7, RegisterNames.A)
            0x80 ->  // RES 0,B
                singleBit.res(0, RegisterNames.B)
            0x81 ->  // RES 0,C
                singleBit.res(0, RegisterNames.C)
            0x82 ->  // RES 0,D
                singleBit.res(0, RegisterNames.D)
            0x83 ->  // RES 0,E
                singleBit.res(0, RegisterNames.E)
            0x84 ->  // RES 0,H
                singleBit.res(0, RegisterNames.H)
            0x85 ->  // RES 0,L
                singleBit.res(0, RegisterNames.L)
            0x86 ->  // RES 0,(HL)
                singleBit.resHL(0, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x87 ->  // RES 0,A
                singleBit.res(0, RegisterNames.A)
            0x88 ->  // RES 1,B
                singleBit.res(1, RegisterNames.B)
            0x89 ->  // RES 1,C
                singleBit.res(1, RegisterNames.C)
            0x8A ->  // RES 1,D
                singleBit.res(1, RegisterNames.D)
            0x8B ->  // RES 1,E
                singleBit.res(1, RegisterNames.E)
            0x8C ->  // RES 1,H
                singleBit.res(1, RegisterNames.H)
            0x8D ->  // RES 1,L
                singleBit.res(1, RegisterNames.L)
            0x8E ->  // RES 1,(HL)
                singleBit.resHL(1, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x8F ->  // RES 1,A
                singleBit.res(1, RegisterNames.A)
            0x90 ->  // RES 2,B
                singleBit.res(2, RegisterNames.B)
            0x91 ->  // RES 2,C
                singleBit.res(2, RegisterNames.C)
            0x92 ->  // RES 2,D
                singleBit.res(2, RegisterNames.D)
            0x93 ->  // RES 2,E
                singleBit.res(2, RegisterNames.E)
            0x94 ->  // RES 2,H
                singleBit.res(2, RegisterNames.H)
            0x95 ->  // RES 2,L
                singleBit.res(2, RegisterNames.L)
            0x96 ->  // RES 2,(HL)
                singleBit.resHL(2, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x97 ->  // RES 2,A
                singleBit.res(2, RegisterNames.A)
            0x98 ->  // RES 3,B
                singleBit.res(3, RegisterNames.B)
            0x99 ->  // RES 3,C
                singleBit.res(3, RegisterNames.C)
            0x9A ->  // RES 3,D
                singleBit.res(3, RegisterNames.D)
            0x9B ->  // RES 3,E
                singleBit.res(3, RegisterNames.E)
            0x9C ->  // RES 3,H
                singleBit.res(3, RegisterNames.H)
            0x9D ->  // RES 3,L
                singleBit.res(3, RegisterNames.L)
            0x9E ->  // RES 3,(HL)
                singleBit.resHL(3, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0x9F ->  // RES 3,A
                singleBit.res(3, RegisterNames.A)
            0xA0 ->  // RES 4,B
                singleBit.res(4, RegisterNames.B)
            0xA1 ->  // RES 4,C
                singleBit.res(4, RegisterNames.C)
            0xA2 ->  // RES 4,D
                singleBit.res(4, RegisterNames.D)
            0xA3 ->  // RES 4,E
                singleBit.res(4, RegisterNames.E)
            0xA4 ->  // RES 4,H
                singleBit.res(4, RegisterNames.H)
            0xA5 ->  // RES 4,L
                singleBit.res(4, RegisterNames.L)
            0xA6 ->  // RES 4,(HL)
                singleBit.resHL(4, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xA7 ->  // RES 4,A
                singleBit.res(4, RegisterNames.A)
            0xA8 ->  // RES 5,B
                singleBit.res(5, RegisterNames.B)
            0xA9 ->  // RES 5,C
                singleBit.res(5, RegisterNames.C)
            0xAA ->  // RES 5,D
                singleBit.res(5, RegisterNames.D)
            0xAB ->  // RES 5,E
                singleBit.res(5, RegisterNames.E)
            0xAC ->  // RES 5,H
                singleBit.res(5, RegisterNames.H)
            0xAD ->  // RES 5,L
                singleBit.res(5, RegisterNames.L)
            0xAE ->  // RES 5,(HL)
                singleBit.resHL(5, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xAF ->  // RES 5,A
                singleBit.res(5, RegisterNames.A)
            0xB0 ->  // RES 6,B
                singleBit.res(6, RegisterNames.B)
            0xB1 ->  // RES 6,C
                singleBit.res(6, RegisterNames.C)
            0xB2 ->  // RES 6,D
                singleBit.res(6, RegisterNames.D)
            0xB3 ->  // RES 6,E
                singleBit.res(6, RegisterNames.E)
            0xB4 ->  // RES 6,H
                singleBit.res(6, RegisterNames.H)
            0xB5 ->  // RES 6,L
                singleBit.res(6, RegisterNames.L)
            0xB6 ->  // RES 6,(HL)
                singleBit.resHL(6, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xB7 ->  // RES 6,A
                singleBit.res(6, RegisterNames.A)
            0xB8 ->  // RES 7,B
                singleBit.res(7, RegisterNames.B)
            0xB9 ->  // RES 7,C
                singleBit.res(7, RegisterNames.C)
            0xBA ->  // RES 7,D
                singleBit.res(7, RegisterNames.D)
            0xBB ->  // RES 7,E
                singleBit.res(7, RegisterNames.E)
            0xBC ->  // RES 7,H
                singleBit.res(7, RegisterNames.H)
            0xBD ->  // RES 7,L
                singleBit.res(7, RegisterNames.L)
            0xBE ->  // RES 7,(HL)
                singleBit.resHL(7, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xBF ->  // RES 7,A
                singleBit.res(7, RegisterNames.A)
            0xC0 ->  // SET 0,B
                singleBit.set(0, RegisterNames.B)
            0xC1 ->  // SET 0,C
                singleBit.set(0, RegisterNames.C)
            0xC2 ->  // SET 0,D
                singleBit.set(0, RegisterNames.D)
            0xC3 ->  // SET 0,E
                singleBit.set(0, RegisterNames.E)
            0xC4 ->  // SET 0,H
                singleBit.set(0, RegisterNames.H)
            0xC5 ->  // SET 0,L
                singleBit.set(0, RegisterNames.L)
            0xC6 ->  // SET 0,(HL)
                singleBit.setHL(0, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xC7 ->  // SET 0,A
                singleBit.set(0, RegisterNames.A)
            0xC8 ->  // SET 1,B
                singleBit.set(1, RegisterNames.B)
            0xC9 ->  // SET 1,C
                singleBit.set(1, RegisterNames.C)
            0xCA ->  // SET 1,D
                singleBit.set(1, RegisterNames.D)
            0xCB ->  // SET 1,E
                singleBit.set(1, RegisterNames.E)
            0xCC ->  // SET 1,H
                singleBit.set(1, RegisterNames.H)
            0xCD ->  // SET 1,L
                singleBit.set(1, RegisterNames.L)
            0xCE ->  // SET 1,(HL)
                singleBit.setHL(1, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xCF ->  // SET 1,A
                singleBit.set(1, RegisterNames.A)
            0xD0 ->  // SET 2,B
                singleBit.set(2, RegisterNames.B)
            0xD1 ->  // SET 2,C
                singleBit.set(2, RegisterNames.C)
            0xD2 ->  // SET 2,D
                singleBit.set(2, RegisterNames.D)
            0xD3 ->  // SET 2,E
                singleBit.set(2, RegisterNames.E)
            0xD4 ->  // SET 2,H
                singleBit.set(2, RegisterNames.H)
            0xD5 ->  // SET 2,L
                singleBit.set(2, RegisterNames.L)
            0xD6 ->  // SET 2,(HL)
                singleBit.setHL(2, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xD7 ->  // SET 2,A
                singleBit.set(2, RegisterNames.A)
            0xD8 ->  // SET 3,B
                singleBit.set(3, RegisterNames.B)
            0xD9 ->  // SET 3,C
                singleBit.set(3, RegisterNames.C)
            0xDA ->  // SET 3,D
                singleBit.set(3, RegisterNames.D)
            0xDB ->  // SET 3,E
                singleBit.set(3, RegisterNames.E)
            0xDC ->  // SET 3,H
                singleBit.set(3, RegisterNames.H)
            0xDD ->  // SET 3,L
                singleBit.set(3, RegisterNames.L)
            0xDE ->  // SET 3,(HL)
                singleBit.setHL(3, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xDF ->  // SET 3,A
                singleBit.set(3, RegisterNames.A)
            0xE0 ->  // SET 4,B
                singleBit.set(4, RegisterNames.B)
            0xE1 ->  // SET 4,C
                singleBit.set(4, RegisterNames.C)
            0xE2 ->  // SET 4,D
                singleBit.set(4, RegisterNames.D)
            0xE3 ->  // SET 4,E
                singleBit.set(4, RegisterNames.E)
            0xE4 ->  // SET 4,H
                singleBit.set(4, RegisterNames.H)
            0xE5 ->  // SET 4,L
                singleBit.set(4, RegisterNames.L)
            0xE6 ->  // SET 4,(HL)
                singleBit.setHL(4, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xE7 ->  // SET 4,A
                singleBit.set(4, RegisterNames.A)
            0xE8 ->  // SET 5,B
                singleBit.set(5, RegisterNames.B)
            0xE9 ->  // SET 5,C
                singleBit.set(5, RegisterNames.C)
            0xEA ->  // SET 5,D
                singleBit.set(5, RegisterNames.D)
            0xEB ->  // SET 5,E
                singleBit.set(5, RegisterNames.E)
            0xEC ->  // SET 5,H
                singleBit.set(5, RegisterNames.H)
            0xED ->  // SET 5,L
                singleBit.set(5, RegisterNames.L)
            0xEE ->  // SET 5,(HL)
                singleBit.setHL(5, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xEF ->  // SET 5,A
                singleBit.set(5, RegisterNames.A)
            0xF0 ->  // SET 6,B
                singleBit.set(6, RegisterNames.B)
            0xF1 ->  // SET 6,C
                singleBit.set(6, RegisterNames.C)
            0xF2 ->  // SET 6,D
                singleBit.set(6, RegisterNames.D)
            0xF3 ->  // SET 6,E
                singleBit.set(6, RegisterNames.E)
            0xF4 ->  // SET 6,H
                singleBit.set(6, RegisterNames.H)
            0xF5 ->  // SET 6,L
                singleBit.set(6, RegisterNames.L)
            0xF6 ->  // SET 6,(HL)
                singleBit.setHL(6, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xF7 ->  // SET 6,A
                singleBit.set(6, RegisterNames.A)
            0xF8 ->  // SET 7,B
                singleBit.set(7, RegisterNames.B)
            0xF9 ->  // SET 7,C
                singleBit.set(7, RegisterNames.C)
            0xFA ->  // SET 7,D
                singleBit.set(7, RegisterNames.D)
            0xFB ->  // SET 7,E
                singleBit.set(7, RegisterNames.E)
            0xFC ->  // SET 7,H
                singleBit.set(7, RegisterNames.H)
            0xFD ->  // SET 7,L
                singleBit.set(7, RegisterNames.L)
            0xFE ->  // SET 7,(HL)
                singleBit.setHL(7, bus.getFromCPU(BusConstants.GET_HL, Bus.EMPTY_ARGUMENTS) as Int)
            0xFF ->  // SET 7,A
                singleBit.set(7, RegisterNames.A)
            else -> {
                println("No OPCode or Lacks Implementation")
                exitProcess(0)
            }
        }

    }
}
