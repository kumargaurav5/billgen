(function(){
  var app = angular.module('invoiceApp', []);

  app.factory('InvoiceSvc', ['$http', function($http){
    var base = 'http://localhost:8080/api/invoices';
    return {
      list: function(){ return $http.get(base); },
      get: function(id){ return $http.get(base + '/' + id); },
      create: function(payload){ return $http.post(base, payload); },
      update: function(id,payload){ return $http.put(base + '/' + id, payload); },
      delete: function(id){ return $http.delete(base + '/' + id); },
      download: function(id){ return $http.get(base + '/' + id + '/pdf', { responseType: 'arraybuffer' }); },
      send: function(id, payload){ return $http.post(base + '/' + id + '/send', payload); }
    };
  }]);

  app.controller('MainCtrl', ['InvoiceSvc', '$window', '$filter', function(InvoiceSvc, $window, $filter){
    var vm = this;
    vm.invoices = [];
    vm.filterText = '';
    vm.filterFn = function(inv){
      if (!vm.filterText) return true;
      return (inv.clientName || '').toLowerCase().indexOf(vm.filterText.toLowerCase()) !== -1;
    };

    vm.gstPercent = 18; // default

    vm.reset = function(){
      vm.editing = false;
      vm.invoice = { items: [], invoiceDate: new Date().toISOString().slice(0,10), status: 'UNPAID', subTotal:0, tax:0, total:0 };
      vm.recalc();
    };

  vm.addItem = function(){ vm.invoice.items.push({ description:'', quantity:1, rate:0 }); vm.recalc(); };
    vm.removeItem = function(idx){ vm.invoice.items.splice(idx,1); vm.recalc(); };

    vm.amount = function(it){ if(!it) return 0; var q = Number(it.quantity)||0; var r = Number(it.rate)||0; return q*r; };

    vm.recalc = function(){
      var sub = 0;
      if (vm.invoice.items) {
        vm.invoice.items.forEach(function(it){ sub += vm.amount(it); });
      }
      vm.invoice.subTotal = Number(sub.toFixed(2));
      var tax = sub * ((Number(vm.gstPercent)||0)/100);
      vm.invoice.tax = Number(tax.toFixed(2));
      vm.invoice.total = Number((sub + tax).toFixed(2));
    };

    vm.save = function(){
      vm.recalc();
      if (vm.editing && vm.invoice.id) {
        InvoiceSvc.update(vm.invoice.id, vm.invoice).then(function(){ vm.load(); vm.reset(); });
      } else {
        InvoiceSvc.create(vm.invoice).then(function(){ vm.load(); vm.reset(); });
      }
    };

    vm.load = function(){
      InvoiceSvc.list().then(function(resp){ vm.invoices = resp.data || []; });
    };

    vm.edit = function(inv){
      vm.editing = true;
      // make a copy to edit
      vm.invoice = angular.copy(inv);
      if(!vm.invoice.items) vm.invoice.items = [];
      vm.gstPercent = (vm.invoice.tax && vm.invoice.subTotal) ? ((vm.invoice.tax / vm.invoice.subTotal) * 100) : vm.gstPercent;
      vm.recalc();
    };

    vm.delete = function(id){ if(!confirm('Delete?')) return; InvoiceSvc.delete(id).then(function(){ vm.load(); }); };

    vm.download = function(id){
      InvoiceSvc.download(id).then(function(resp){
        var blob = new Blob([resp.data], { type: 'application/pdf' });
        var url = URL.createObjectURL(blob);
        var a = document.createElement('a'); a.href = url; a.download = 'invoice-' + id + '.pdf'; a.click(); URL.revokeObjectURL(url);
      }, function(err){ alert('Download error'); });
    };

    vm.send = function(id){
      var to = prompt('Recipient email address:'); if(!to) return;
      InvoiceSvc.send(id, { to: to }).then(function(){ vm.toast('Email sent'); }, function(){ vm.toast('Email failed', true); });
    };

    vm.toast = function(msg, isError){
      // simple toast using alert for now; could be improved
      if(isError) $window.alert('Error: ' + msg); else $window.alert(msg);
    };

    vm.formatCurrency = function(val){
      if (val === undefined || val === null) return '$0.00';
      return $filter('currency')(val, '$', 2);
    };

    // init
    vm.reset();
    vm.load();
  }]);
})();
