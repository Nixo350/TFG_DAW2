// src/app/common/confirmation-dialog/confirmation-dialog.component.ts
import { Component, Inject } from '@angular/core';
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-confirmation-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatDialogModule, // Necesario para componentes de diálogo
    MatButtonModule  // Necesario para los botones
  ],
  template: `
    <h2 mat-dialog-title>{{ data.title }}</h2>
    <mat-dialog-content>
      <p>{{ data.message }}</p>
    </mat-dialog-content>
    <mat-dialog-actions align="end">
      <button mat-button (click)="onCancel()">{{ data.cancelText || 'Cancelar' }}</button>
      <button mat-raised-button color="warn" (click)="onConfirm()">{{ data.confirmText || 'Eliminar' }}</button>
    </mat-dialog-actions>
  `,
  styleUrls: ['./confirmation-dialog.component.css']
})
export class ConfirmationDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<ConfirmationDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { title: string; message: string; cancelText?: string; confirmText?: string }
  ) {}

  onConfirm(): void {
    this.dialogRef.close(true); // Cierra el diálogo y devuelve 'true' (confirmado)
  }

  onCancel(): void {
    this.dialogRef.close(false); // Cierra el diálogo y devuelve 'false' (cancelado)
  }
}