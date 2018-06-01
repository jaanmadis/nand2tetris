program assembler;

{$APPTYPE CONSOLE}

uses
  SysUtils, Classes, Generics.Collections;

type
  TSymbol = record
    Name: String;
    Address: Integer;
  end;

const
  PREDEFINED_SYMBOLS: array[0..22] of TSymbol = (
    (Name: ('SP'); Address: 0),
    (Name: ('LCL'); Address: 1),
    (Name: ('ARG'); Address: 2),
    (Name: ('THIS'); Address: 3),
    (Name: ('THAT'); Address: 4),
    (Name: ('R0'); Address: 0),
    (Name: ('R1'); Address: 1),
    (Name: ('R2'); Address: 2),
    (Name: ('R3'); Address: 3),
    (Name: ('R4'); Address: 4),
    (Name: ('R5'); Address: 5),
    (Name: ('R6'); Address: 6),
    (Name: ('R7'); Address: 7),
    (Name: ('R8'); Address: 8),
    (Name: ('R9'); Address: 9),
    (Name: ('R10'); Address: 10),
    (Name: ('R11'); Address: 11),
    (Name: ('R12'); Address: 12),
    (Name: ('R13'); Address: 13),
    (Name: ('R14'); Address: 14),
    (Name: ('R15'); Address: 15),
    (Name: ('SCREEN'); Address: 16384),
    (Name: ('KBD'); Address: 24576));

var
  Symbols: array of TSymbol;
  VariableOffset: Integer;

function IntToBin(Value: Longint; Digits: Integer): string;
var
  i: Integer;
begin
  Result := '';
  for i := Digits - 1 downto 0 do
    if Value and (1 shl i) <> 0 then
      Result := Result + '1'
    else
      Result := Result + '0';
end;

procedure InitSymbols;
var
  i: Integer;
begin
  SetLength(Symbols, Length(PREDEFINED_SYMBOLS));
  for i := Low(PREDEFINED_SYMBOLS) to High(PREDEFINED_SYMBOLS) do
    Symbols[i] := PREDEFINED_SYMBOLS[i];
  VariableOffset := 16;
end;

function GetSymbolAddress(Name: String): Integer;
var
  i: Integer;
begin
  Result := -1;
  for i := Low(Symbols) to High(Symbols) do
    if Symbols[i].Name = Name then
      Exit(Symbols[i].Address);
end;

function StripComment(Line: String): String;
var
  PosComment: Integer;
begin
  Result := Line;
  PosComment := Pos('//', Result);
  if PosComment > 0 then
    Result := Copy(Result, 1, PosComment - 1);
end;

function StripChar(Line: String; Ch: Char): String;
var
  PosChar: Integer;
begin
  Result := Line;
  repeat
    PosChar := Pos(Ch, Result);
    if PosChar > 0 then
      Delete(Result, PosChar, 1);
  until PosChar = 0;
end;

function ParsedLabelSymbol(Line: String; Address: Integer): Boolean;
var
  LabelSymbol: TSymbol;
begin
  Result := False;
  if (Pos('(', Line) = 1) and (Pos(')', Line) <> 0) then
  begin
    LabelSymbol.Name := Copy(Line, 2, Length(Line) - 2);
    LabelSymbol.Address := Address;
    SetLength(Symbols, Length(Symbols) + 1);
    Symbols[Length(Symbols) - 1] := LabelSymbol;
    Result := True;
  end;
end;

function ParsedAInstruction(Line: String): String;
var
  Name: String;
  NameInt: Integer;
  Address: Integer;
begin
  Result := '0';
  Name := Copy(Line, 2, Length(Line) - 1);
  NameInt := StrToIntDef(Name, -1);
  if NameInt <> -1 then
    Result := Result + IntToBin(NameInt, 15)
  else
  begin
    Address := GetSymbolAddress(Name);
    if Address <> -1 then
      Result := Result + IntToBin(Address, 15)
    else
    begin
      Result := Result + IntToBin(VariableOffset, 15);
      SetLength(Symbols, Length(Symbols) + 1);
      Symbols[Length(Symbols) - 1].Name := Name;
      Symbols[Length(Symbols) - 1].Address := VariableOffset;
      Inc(VariableOffset);
    end;
  end;
end;

function GetDest(Dest: String): String;
begin
  Result := '000';
  if Dest = 'M' then
    Result := '001'
  else if Dest = 'D' then
    Result := '010'
  else if Dest = 'MD' then
    Result := '011'
  else if Dest = 'A' then
    Result := '100'
  else if Dest = 'AM' then
    Result := '101'
  else if Dest = 'AD' then
    Result := '110'
  else if Dest = 'AMD' then
    Result := '111'
end;

function GetComp(Dest: String): String;
var
  PosA, PosM: Integer;
begin
  PosA := Pos('A', Dest);
  PosM := Pos('M', Dest);
  if PosA > 0 then
    Dest[PosA] := 'X';
  if PosM > 0 then
    Dest[PosM] := 'X';
  if Dest = '0' then 
    Result := '101010';
  if Dest = '1' then 
    Result := '111111';
  if Dest = '-1' then 
    Result := '111010';
  if Dest = 'D' then 
    Result := '001100';
  if Dest = 'X' then 
    Result := '110000';
  if Dest = '!D' then 
    Result := '001101';
  if Dest = '!X' then 
    Result := '110001';
  if Dest = '-D' then 
    Result := '001111';
  if Dest = '-X' then 
    Result := '110011';
  if Dest = 'D+1' then 
    Result := '011111';
  if Dest = 'X+1' then 
    Result := '110111';
  if Dest = 'D-1' then 
    Result := '001110';
  if Dest = 'X-1' then 
    Result := '110010';
  if Dest = 'D+X' then 
    Result := '000010';
  if Dest = 'D-X' then 
    Result := '010011';
  if Dest = 'X-D' then 
    Result := '000111';
  if Dest = 'D&X' then 
    Result := '000000';
  if Dest = 'D|X' then 
    Result := '010101'; 
  if PosM > 0 then
    Result := '1' + Result 
  else
    Result := '0' + Result; 
end;

function GetJump(Dest: String): String;
begin
  Result := '000';
  if Dest = 'JGT' then
    Result := '001'
  else if Dest = 'JEQ' then
    Result := '010'
  else if Dest = 'JGE' then
    Result := '011'
  else if Dest = 'JLT' then
    Result := '100'
  else if Dest = 'JNE' then
    Result := '101'
  else if Dest = 'JLE' then
    Result := '110'
  else if Dest = 'JMP' then
    Result := '111'
end;

function ParsedCInstruction(Line: String): String;
var
  Dest: String;
  Comp: String;
  Jump: String;
  PosComp: Integer;
  PosJump: Integer;
begin
  Result := '111';
  PosComp := Pos('=', Line);
  PosJump := Pos(';', Line);
  Dest := Copy(Line, 1, PosComp - 1);
  if PosJump > 0 then
  begin
    Comp := Copy(Line, PosComp + 1, PosJump - 1);
    Jump := Copy(Line, PosJump + 1, Length(Line) - PosJump);
  end
  else
    Comp := Copy(Line, PosComp + 1, Length(Line) - PosComp);
  Result := Result + GetComp(Comp) + GetDest(Dest) + GetJump(Jump);
end;

procedure ReadInput(SrcFileName, DestFileName: String);
var
  List: TStringList;
  Line: String;
  i: Integer;
begin
  List := TStringList.Create;
  try
    List.LoadFromFile(SrcFileName);
    for i := List.Count - 1 downto 0 do
    begin
      Line := List[i];
      Line := StripComment(Line);
      Line := StripChar(Line, Char(9));
      Line := StripChar(Line, Char(32));
      if Line = '' then
        List.Delete(i)
      else
        List[i] := Line;
    end;

    i := 0;
    while i < List.Count do
    begin
      if ParsedLabelSymbol(List[i], i) then
        List.Delete(i)
      else
        Inc(i);
    end;

    for i := 0 to List.Count - 1 do
    begin
      if List[i][1] = '@' then
        List[i] := ParsedAInstruction(List[i])
      else
        List[i] := ParsedCInstruction(List[i])
    end;
  finally
    List.SaveToFile(DestFileName);
    List.Free;
  end;
end;

procedure WriteHackFile(Name: String);
var
  f: TextFile;
begin
  AssignFile(f, Name + '.hack');
  Rewrite(f);
  CloseFile(f);
end;

begin
  InitSymbols;
  ReadInput(ParamStr(1), Copy(ParamStr(1), 1, LastDelimiter('.', ParamStr(1)) - 1) + '.hack');
end.
