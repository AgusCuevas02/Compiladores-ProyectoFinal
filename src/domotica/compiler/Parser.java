package domotica.compiler;

// --- CAMBIO 1: Importar TODAS las clases del AST ---
import domotica.ast.*;
import java.util.ArrayList;
import java.util.List;
// --- FIN CAMBIO 1 ---

public class Parser {
	public static final int _EOF = 0;
	public static final int _number = 1;
	// ... (el resto de las constantes _ident, _string, etc. siguen igual)
	public static final int _color = 5;
	public static final int maxT = 53;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t;   // last recognized token
	public Token la;  // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	// --- MODIFICACION 2: Anadir la lista del AST ---
	private List<ASTNode> ast;

	public List<ASTNode> getAST() {
		return ast;
	}
	// --- FIN MODIFICACION 2 ---
	
	// --- Metodo publico para obtener el conteo de errores ---
	public int getErrorCount() {
		return errors.count;
	}
	
	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
		
		// --- MODIFICACION 3: Inicializar la lista ---
		this.ast = new ArrayList<ASTNode>();
		// --- FIN MODIFICACION 3 ---
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void DOMOTICA() {
		
		while (StartOf(1)) {
			Statement();
		}
	}

	void Statement() {
		
		switch (la.kind) {
		case 6: case 7: case 8: case 9: case 10: case 11: case 12: case 13: case 14: case 15: case 16: {
			DeviceCommand();
			break;
		}
		case 30: {
			Conditional();
			break;
		}
		case 36: case 39: {
			Loop();
			break;
		}
		case 42: {
			VariableDecl();
			break;
		}
		case 44: {
			RoutineDecl();
			break;
		}
		case 48: {
			WaitCommand();
			break;
		}
		case 52: {
			LogCommand();
			break;
		}
		default: SynErr(54); break;
		}
	}

	void DeviceCommand() {
		
		switch (la.kind) {
		case 6: {
			Get();
			OnCommands();
			break;
		}
		case 7: {
			Get();
			OffCommands();
			break;
		}
		case 8: {
			Get();
			SetCommands();
			break;
		}
		case 9: {
			Get();
			LightColor();
			break;
		}
		case 10: {
			Get();
			LightDim();
			break;
		}
		
		case 11: {
			Get();
			SecurityAlarm();
			break;
		}
		case 12: {
			Get();
			SecurityAlarm();
			break;
		}
		case 13: {
			Get();
			SecurityDoor();
			break;
		}
		case 14: {
			Get();
			SecurityDoor();
			break;
		}
		case 15: {
			Get();
			BlindsOpenClose();
			break;
		}
		case 16: {
			Get();
			BlindsOpenClose();
			break;
		}
		default: SynErr(55); break;
		}
	}

	void Conditional() {
		
		Expect(30);
		Expect(31);
		Expect(3);
		Expect(32);
		Expect(33);
		while (StartOf(1)) {
			Statement();
		}
		if (la.kind == 34) {
			Get();
			while (StartOf(1)) {
				Statement();
			}
		}
		Expect(35);
	}

	void Loop() {
		
		if (la.kind == 36) {
			WhileLoop();
		} else if (la.kind == 39) {
			ForLoop();
		} else SynErr(56);
	}

	void VariableDecl() {
		
		Expect(42);
		Expect(3);
		Expect(43);
		Expect(1);
	}

	void RoutineDecl() {
		
		Expect(44);
		Expect(3);
		Expect(45);
		Expect(31);
		if (la.kind == 3) {
			Get();
			while (la.kind == 46) {
				Get();
				Expect(3);
			}
		}
		Expect(32);
		Expect(37);
		while (StartOf(1)) {
			Statement();
		}
		Expect(47);
	}

	// ******************************************************
	// MODIFICACION 4 (CORREGIDA)
	// ******************************************************
	void WaitCommand() {
    Expect(48); // "WAIT"
    Expect(1); // number
    int waitTime = Integer.parseInt(t.val); // Captura el numero
    
    // **PROBLEMA AQUI**: necesitas capturar la unidad ANTES de llamar TimeUnit()
    String timeUnit;
    if (la.kind == 49 || la.kind == 50 || la.kind == 51) {
        timeUnit = la.val; // Captura la unidad del lookahead
    } else {
        timeUnit = "SECONDS"; // Valor por defecto
    }
    
    TimeUnit(); // Consume el token de unidad
    
    ast.add(new WaitCommand(waitTime, timeUnit));
}

	void LogCommand() {
		Expect(52); // "LOG"
		if (la.kind == 4) { // string
			Get();
		} else if (la.kind == 3) { // ident
			Get();
		} else SynErr(57);
		// --- MODIFICACION 5: Agregar logica de LogCommand ---
		// Quita las comillas del string ("Hola") -> Hola
		String logMessage = t.val.startsWith("\"") ? t.val.substring(1, t.val.length() - 1) : t.val;
		ast.add(new LogCommand(logMessage));
		// --- FIN MODIFICACION 5 ---
	}

	void OnCommands() {
		if (la.kind == 17) { // "LIGHT"
			Get();
			Expect(1); // number
			// --- MODIFICACION 6: Agregar logica de LightOn ---
			ast.add(new LightOnCommand(Integer.parseInt(t.val)));
			// --- FIN MODIFICACION 6 ---
		} else if (la.kind == 18) { // "FAN"
			Get();
			Expect(1); // number
			// --- MODIFICACION 7: Agregar logica de FanOn  ---
			ast.add(new FanOnCommand(Integer.parseInt(t.val)));
			// --- FIN MODIFICACION 7 ---
		} else if (la.kind == 21) { // "THERMOSTAT"
			Get();
			Expect(1); // number	
			ast.add(new ThermostatOnCommand(Integer.parseInt(t.val)));
		}
		else SynErr(58);
	}

	void OffCommands() {
		if (la.kind == 17) { // "LIGHT"
			Get();
			Expect(1); // number
			
			ast.add(new LightOffCommand(Integer.parseInt(t.val)));
			
		} else if (la.kind == 18) { // "FAN"
			Get();
			Expect(1); // number
			
			ast.add(new FanOffCommand(Integer.parseInt(t.val)));
			
		} else if (la.kind == 21) { // "THERMOSTAT"
			Get();
			Expect(1); // number
			
			ast.add(new ThermostatOffCommand(Integer.parseInt(t.val)));
			
		} 
		else SynErr(59);
	}

	void SetCommands() {
		if (la.kind == 18) { // "FAN"
			FanSet();
		} else if (la.kind == 21) {
			ThermostatCommand();
		} else if (la.kind == 26) {
			BlindsSet();
		} else SynErr(60);
	}

	void LightColor() {
		Expect(17); // "LIGHT"
		Expect(1); // number
		int id = Integer.parseInt(t.val); // Captura el ID
		
		Expect(19); // "TO"
		if (la.kind == 5) { // color
			Get();
		} else if (la.kind == 3) { // ident
			Get();
		} else SynErr(61);
		
		// --- MODIFICACION 10: Agregar logica de ColorCommand ---
		ast.add(new ColorCommand(id, t.val));
		// --- FIN MODIFICACION 10 ---
	}

	void LightDim() {
		Expect(17);
		Expect(1);
		// (Agregar logica de LightDimCommand si existe)
	}

	void SecurityAlarm() {
		Expect(27);
		if (la.kind == 28) {
			Get();
			Expect(3);
		}
	}

	void SecurityDoor() {
		Expect(29);
		Expect(1);
	}

	void BlindsOpenClose() {
		Expect(26);
		Expect(1);
	}

	void FanSet() {
		Expect(18); // "FAN"
		Expect(1); // number
		int id = Integer.parseInt(t.val); // Captura el ID
		int speed = 0; // Velocidad por defecto
		
		if (la.kind == 20) { // "SPEED"
			Get();
			Expect(1); // number
			speed = Integer.parseInt(t.val); // Captura la velocidad
		}
		
		// --- MODIFICACION 11: Agregar logica de SetFanSpeed  ---
		ast.add(new SetFanSpeedCommand(id, speed));
		// --- FIN MODIFICACION 11 ---
	}

	void ThermostatCommand() {
    Expect(21); // "THERMOSTAT"

    // Caso esperado: THERMOSTAT <number> TEMPERATURE <number>
    if (la.kind == 1) {           // number (room)
        Get();
        int room = Integer.parseInt(t.val);

        // Si lo que sigue es la palabra TEMPERATURE (puede venir como token de palabra clave o ident)
        if (la.val != null && la.val.equalsIgnoreCase("TEMPERATURE")) {
            // Consumir el token "TEMPERATURE" (puede ser una palabra clave o un ident)
            Get();
            Expect(1); // number
            int temp = Integer.parseInt(t.val);
            // Crear el nodo AST para setear temperatura
            ast.add(new ThermostatSetTemperatureCommand(room, temp));
            return;
        }

        // Si lo que sigue no es TEMPERATURE, intentar aceptar la forma legacy (TO <number> [MODE ...])
        if (la.kind == 19) { // "TO"
            Get(); // consumir TO
            Expect(1); // number (valor)
            if (la.kind == 22) { // "MODE"
                Get();
                if (la.kind == 23 || la.kind == 24 || la.kind == 25) { // HEAT|COOL|AUTO
                    Get();
                } else SynErr(62);
            }
            return;
        }

        // Si no hubo TEMPERATURE ni TO, error de sintaxis
        SynErr(62);
        return;
    }

    if (la.kind == 19) { // "TO"
        Get();
        Expect(1); // number
        if (la.kind == 22) { // optional MODE ...
            Get();
            if (la.kind == 23 || la.kind == 24 || la.kind == 25) {
                Get();
            } else SynErr(62);
        }
        return;
    }

    // Si no encaja en nada, error
    SynErr(62);
}

	void BlindsSet() {
		Expect(26);
		Expect(1);
		if (la.kind == 19) {
			Get();
			Expect(1);
		}
	}

	void WhileLoop() {
		Expect(36);
		Expect(31);
		Expect(3);
		Expect(32);
		Expect(37);
		while (StartOf(1)) {
			Statement();
		}
		Expect(38);
	}

	void ForLoop() {
		Expect(39);
		Expect(3);
		Expect(40);
		Expect(1);
		Expect(19);
		Expect(1);
		Expect(37);
		while (StartOf(1)) {
			Statement();
		}
		Expect(41);
	}

	void TimeUnit() {
		if (la.kind == 49) {
			Get();
		} else if (la.kind == 50) {
			Get();
		} else if (la.kind == 51) {
			Get();
		} else SynErr(63);
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		DOMOTICA();
		Expect(0);

		scanner.buffer.Close();
	}

	private static final boolean[][] set = {
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x},
		{_x,_x,_x,_x, _x,_x,_T,_T, _T,_T,_T,_T, _T,_T,_T,_T, _T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_T,_x, _x,_x,_x,_x, _T,_x,_x,_T, _x,_x,_T,_x, _T,_x,_x,_x, _T,_x,_x,_x, _T,_x,_x}

	};
} // end Parser


class Errors {
	public int count = 0;
	public java.io.PrintStream errorStream = System.out;
	public String errMsgFormat = "-- line {0} col {1}: {2}";
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "number expected"; break;
			case 2: s = "decimal expected"; break;
			case 3: s = "ident expected"; break;
			case 4: s = "string expected"; break;
			case 5: s = "color expected"; break;
			case 6: s = "\"ON\" expected"; break;
			case 7: s = "\"OFF\" expected"; break;
			case 8: s = "\"SET\" expected"; break;
			case 9: s = "\"COLOR\" expected"; break;
			case 10: s = "\"DIM\" expected"; break;
			case 11: s = "\"ARM\" expected"; break;
			case 12: s = "\"DISARM\" expected"; break;
			case 13: s = "\"LOCK\" expected"; break;
			case 14: s = "\"UNLOCK\" expected"; break;
			case 15: s = "\"OPEN\" expected"; break;
			case 16: s = "\"CLOSE\" expected"; break;
			case 17: s = "\"LIGHT\" expected"; break;
			case 18: s = "\"FAN\" expected"; break;
			case 19: s = "\"TO\" expected"; break;
			case 20: s = "\"SPEED\" expected"; break;
			case 21: s = "\"THERMOSTAT\" expected"; break;
			case 22: s = "\"MODE\" expected"; break;
			case 23: s = "\"HEAT\" expected"; break;
			case 24: s = "\"COOL\" expected"; break;
			case 25: s = "\"AUTO\" expected"; break;
			case 26: s = "\"BLINDS\" expected"; break;
			case 27: s = "\"ALARM\" expected"; break;
			case 28: s = "\"SYSTEM\" expected"; break;
			case 29: s = "\"DOOR\" expected"; break;
			case 30: s = "\"IF\" expected"; break;
			case 31: s = "\"(\" expected"; break;
			case 32: s = "\")\" expected"; break;
			case 33: s = "\"THEN\" expected"; break;
			case 34: s = "\"ELSE\" expected"; break;
			case 35: s = "\"ENDIF\" expected"; break;
			case 36: s = "\"WHILE\" expected"; break;
			case 37: s = "\"DO\" expected"; break;
			case 38: s = "\"ENDWHILE\" expected"; break;
			case 39: s = "\"FOR\" expected"; break;
			case 40: s = "\"FROM\" expected"; break;
			case 41: s = "\"ENDFOR\" expected"; break;
			case 42: s = "\"VAR\" expected"; break;
			case 43: s = "\"=\" expected"; break;
			case 44: s = "\"ROUTINE\" expected"; break;
			case 45: s = "\"PARAMS\" expected"; break;
			case 46: s = "\",\" expected"; break;
			case 47: s = "\"ENDROUTINE\" expected"; break;
			case 48: s = "\"WAIT\" expected"; break;
			case 49: s = "\"SECONDS\" expected"; break;
			case 50: s = "\"MINUTES\" expected"; break;
			case 51: s = "\"HOURS\" expected"; break;
			case 52: s = "\"LOG\" expected"; break;
			case 53: s = "??? expected"; break;
			case 54: s = "invalid Statement"; break;
			case 55: s = "invalid DeviceCommand"; break;
			case 56: s = "invalid Loop"; break;
			case 57: s = "invalid LogCommand"; break;
			case 58: s = "invalid OnCommands"; break;
			case 59: s = "invalid OffCommands"; break;
			case 60: s = "invalid SetCommands"; break;
			case 61: s = "invalid LightColor"; break;
			case 62: s = "invalid ThermostatCommand"; break;
			case 63: s = "invalid TimeUnit"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}