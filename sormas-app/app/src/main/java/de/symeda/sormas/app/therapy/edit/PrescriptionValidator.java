/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.therapy.edit;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.app.databinding.FragmentPrescriptionEditLayoutBinding;
import de.symeda.sormas.app.util.Callback;

public final class PrescriptionValidator {

    public static void initializeValidation(final FragmentPrescriptionEditLayoutBinding contentBinding) {
        Callback prescriptionStartCallback = () -> {
            if (contentBinding.prescriptionPrescriptionStart.getValue() != null && contentBinding.prescriptionPrescriptionEnd.getValue() != null) {
                if (contentBinding.prescriptionPrescriptionStart.getValue().after(contentBinding.prescriptionPrescriptionEnd.getValue())) {
                    contentBinding.prescriptionPrescriptionStart.enableErrorState(
                            I18nProperties.getValidationError(Validations.beforeDate,
                                    contentBinding.prescriptionPrescriptionStart.getCaption(),
                                    contentBinding.prescriptionPrescriptionEnd.getCaption()));
                }
            }
        };

        Callback prescriptionEndCallback = () -> {
            if (contentBinding.prescriptionPrescriptionStart.getValue() != null && contentBinding.prescriptionPrescriptionEnd.getValue() != null) {
                if (contentBinding.prescriptionPrescriptionEnd.getValue().before(contentBinding.prescriptionPrescriptionStart.getValue())) {
                    contentBinding.prescriptionPrescriptionEnd.enableErrorState(
                            I18nProperties.getValidationError(Validations.afterDate,
                                    contentBinding.prescriptionPrescriptionEnd.getCaption(),
                                    contentBinding.prescriptionPrescriptionStart.getCaption()));
                }
            }
        };

        contentBinding.prescriptionPrescriptionStart.setValidationCallback(prescriptionStartCallback);
        contentBinding.prescriptionPrescriptionEnd.setValidationCallback(prescriptionEndCallback);
    }

}