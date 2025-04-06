$(document).ready(function() {
    // Sample VIN-to-part data (replace with actual database calls later)
    const vinParts = {
        'C50': [
            { part_id: 'P001', part_name: 'Oil Filter', compatibility_notes: 'Fits all Honda C50 models' },
            { part_id: 'P002', part_name: 'Air Filter', compatibility_notes: 'Compatible with C50 models (2005-2020)' }
        ],
        'C90': [
            { part_id: 'P003', part_name: 'Brake Pads', compatibility_notes: 'Fits front brake system for C90' },
            { part_id: 'P004', part_name: 'Spark Plugs', compatibility_notes: 'Works for C90 models (2010-2022)' }
        ],
        'C100': [
            { part_id: 'P005', part_name: 'Chain', compatibility_notes: 'For Honda C100 only' },
            { part_id: 'P006', part_name: 'Tire', compatibility_notes: 'Fits C100 rear tire' }
        ]
    };

    // Event listener for lookup button
    $('#lookup-btn').click(function() {
        const vinNumber = $('#vin-number').val().trim();

        if (vinNumber) {
            // Clear previous results
            $('#vin-results-body').empty();

            // Check if VIN exists in sample data
            if (vinParts[vinNumber]) {
                // Populate results
                vinParts[vinNumber].forEach(part => {
                    $('#vin-results-body').append(`
                        <tr>
                            <td>${part.part_id}</td>
                            <td>${part.part_name}</td>
                            <td>${part.compatibility_notes}</td>
                        </tr>
                    `);
                });
            } else {
                $('#vin-results-body').append('<tr><td colspan="3">No compatible parts found for this VIN.</td></tr>');
            }
        } else {
            alert('Please enter a valid VIN number.');
        }
    });
});
