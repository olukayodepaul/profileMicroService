package dart.dartProfile.darts_app.controller;


import dart.dartProfile.darts_app.entity.AddAddressReqModel;
import dart.dartProfile.darts_app.entity.AddProfileReqResModel;
import dart.dartProfile.darts_app.entity.FetchAddressResModel;
import dart.dartProfile.darts_app.service.address.AddAddressService;
import dart.dartProfile.darts_app.service.address.DeleteAddressService;
import dart.dartProfile.darts_app.service.address.FetchAddressService;
import dart.dartProfile.darts_app.service.address.UpdateAddressService;
import dart.dartProfile.utilities.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AddressController {

    private final AddAddressService addAddress;
    private final DeleteAddressService deleteAddress;
    private final FetchAddressService fetchAddressService;
    private final UpdateAddressService updateAddressService;


    public AddressController(AddAddressService addAddress, DeleteAddressService deleteAddress, FetchAddressService fetchAddressService, UpdateAddressService updateAddressService) {
        this.addAddress = addAddress;
        this.deleteAddress = deleteAddress;
        this.fetchAddressService = fetchAddressService;
        this.updateAddressService = updateAddressService;
    }

    @PostMapping("/address")
    public ResponseEntity<ResponseHandler> addAddress(
            @RequestBody List<AddAddressReqModel> request,
            @RequestHeader("Authorization") String token
    ) {
        return addAddress.addUserAddress(request, token);
    }

    @DeleteMapping("/address/{id}")
    public ResponseEntity<ResponseHandler> deleteAddress(
            @PathVariable("id") int  id,
            @RequestHeader("Authorization") String token
    ){
        return deleteAddress.deleteAddress(id, token);
    }

    @GetMapping("/address")
    public ResponseEntity<FetchAddressResModel> getAddress(
            @RequestHeader("Authorization") String token
    ) {
        return fetchAddressService.getAddress(token);
    }

    @PutMapping("/address/{id}")
    public ResponseEntity<ResponseHandler> updateAddress(
            @PathVariable("id") Integer id,
            @RequestBody AddProfileReqResModel request,
            @RequestHeader("Authorization") String token

    ){
        return updateAddressService.updateAddress(id, request, token);
    }




}
