import { Directive, ElementRef, HostListener, OnDestroy, OnInit, inject } from '@angular/core';
import { NgControl } from '@angular/forms';
import { Subscription } from 'rxjs';

@Directive({
  selector: '[appCpfMask]',
  standalone: true
})
export class CpfMaskDirective implements OnInit, OnDestroy {
  private readonly control = inject(NgControl);
  private readonly elementRef = inject(ElementRef<HTMLInputElement>);
  private subscription?: Subscription;

  ngOnInit(): void {
    this.applyMask(this.control.control?.value);
    this.subscription = this.control.control?.valueChanges.subscribe((value) => {
      const digits = this.onlyDigits(value);
      const currentDisplay = this.elementRef.nativeElement.value;
      if (this.onlyDigits(currentDisplay) !== digits) {
        this.elementRef.nativeElement.value = this.format(digits);
      }
    });
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
  }

  @HostListener('input', ['$event'])
  onInput(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digits = this.onlyDigits(input.value).slice(0, 11);
    input.value = this.format(digits);
    this.control.control?.setValue(digits, { emitEvent: false });
  }

  private applyMask(value: unknown): void {
    const digits = this.onlyDigits(value).slice(0, 11);
    if (!digits) {
      return;
    }
    this.elementRef.nativeElement.value = this.format(digits);
    this.control.control?.setValue(digits, { emitEvent: false });
  }

  private onlyDigits(value: unknown): string {
    return String(value ?? '').replace(/\D/g, '');
  }

  private format(digits: string): string {
    if (digits.length <= 3) {
      return digits;
    }
    if (digits.length <= 6) {
      return `${digits.slice(0, 3)}.${digits.slice(3)}`;
    }
    if (digits.length <= 9) {
      return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6)}`;
    }
    return `${digits.slice(0, 3)}.${digits.slice(3, 6)}.${digits.slice(6, 9)}-${digits.slice(9)}`;
  }
}
